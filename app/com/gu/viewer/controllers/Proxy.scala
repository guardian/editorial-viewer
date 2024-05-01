package com.gu.viewer.controllers

import com.gu.pandomainauth.PanDomainAuthSettingsRefresher
import com.gu.viewer.config.AppConfig
import com.gu.viewer.logging.Loggable
import com.gu.viewer.proxy._
import play.api.libs.ws.WSClient
import play.api.mvc.{BaseController, ControllerComponents}

import scala.concurrent.Future


class Proxy(
  val controllerComponents: ControllerComponents,
  val wsClient: WSClient,
  previewProxy: PreviewProxy,
  liveProxy: LiveProxy,
  val config: AppConfig,
  val panDomainSettings: PanDomainAuthSettingsRefresher
) extends BaseController with Loggable with PanDomainAuthActions {

  private val pandaCookieName = panDomainSettings.settings.cookieSettings.cookieName

  def proxy(service: String, path: String) = AuthAction.async { implicit request =>

    ProxyRequest(service, path, request) match {
      case r: PreviewProxyRequest => previewProxy.proxy(r.copy(maybePandaCookieToForward = request.cookies.get(pandaCookieName)))
      case r: LiveProxyRequest => liveProxy.proxy(r)
      case UnknownProxyRequest => Future.successful(BadRequest(s"Unknown proxy service: $service"))
    }
  }

  /**
   * Preview Authentication callback.
   *
   * Proxy all request params and Preview session cookie to Preview authentication callback.
   * Store response cookies into Viewer's play session.
   */
  def previewAuthCallback = AuthAction.async { request =>
    previewProxy.previewAuthCallback(PreviewProxyRequest.authCallbackRequest(request))
  }


  /**
   * Redirect requests to routes that don't exist which originate (Referer header)
   * from a proxied request. Catches server relative requests in a proxied response.
   */
  def redirectRelative(path: String) = AuthAction { request =>

    val fromProxy = """^\w+:\/\/([^/]+)\/proxy\/([^/]+).*$""".r
    val host = request.host

    request.headers.get("Referer") match {
      case Some(fromProxy(`host`, service)) =>
        Redirect(routes.Proxy.proxy(service, request.uri.tail))

      case _ => NotFound(s"Resource not found: $path")
    }
  }

  /** We don't want to redirect for POST requests */

  def catchRelativePost(path: String) = AuthAction.async { implicit request =>

    val fromProxy = """^\w+:\/\/([^/]+)\/proxy\/([^/]+).*$""".r
    val host = request.host

    request.headers.get("Referer") match {
      case Some(fromProxy(`host`, service)) => {
        val body = request.body.asFormUrlEncoded

        ProxyRequest(service, path, request, body) match {
          case r: PreviewProxyRequest => previewProxy.proxyPost(r)
          case r: LiveProxyRequest => liveProxy.proxyPost(r)
          case UnknownProxyRequest => Future.successful(BadRequest(s"Unknown proxy service: $service"))
        }
      }
      case _ => Future.successful(NotFound(s"Resource not found: $path"))
    }
  }
}
