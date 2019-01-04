package com.gu.viewer.controllers

import com.gu.viewer.logging.Loggable
import com.gu.viewer.proxy._
import javax.inject.Inject
import play.api.mvc.{Action, BaseController, Controller, ControllerComponents}

import scala.concurrent.Future


class Proxy(previewProxy: PreviewProxy, liveProxy: LiveProxy, override val controllerComponents: ControllerComponents)
  extends BaseController with Loggable {

  def proxy(service: String, path: String) = Action.async { implicit request =>

    ProxyRequest(service, path, request) match {
      case r: PreviewProxyRequest => previewProxy.proxy(r)
      case r: LiveProxyRequest => liveProxy.proxy(r)
      case UnknownProxyRequest => Future.successful(BadRequest(s"Unknown proxy service: $service"))
    }
  }

  def proxyPost(service: String, path: String) = Action.async { implicit request =>

    val body = request.body.asFormUrlEncoded

    ProxyRequest(service, path, request, body) match {
      case r: PreviewProxyRequest => previewProxy.proxyPost(r)
      case r: LiveProxyRequest => liveProxy.proxyPost(r)
      case UnknownProxyRequest => Future.successful(BadRequest(s"Unknown proxy service: $service"))
    }
  }


  /**
   * Preview Authentication callback.
   *
   * Proxy all request params and Preview session cookie to Preview authentication callback.
   * Store response cookies into Viewer's play session.
   */
  def previewAuthCallback = Action.async { request =>
    previewProxy.previewAuthCallback(PreviewProxyRequest.authCallbackRequest(request))
  }


  /**
   * Redirect requests to routes that don't exist which originate (Referer header)
   * from a proxied request. Catches server relative requests in a proxied response.
   */
  def redirectRelative(path: String) = Action { request =>

    val fromProxy = """^\w+:\/\/([^/]+)\/proxy\/([^/]+).*$""".r
    val host = request.host

    request.headers.get("Referer") match {
      case Some(fromProxy(`host`, service)) =>
        Redirect(routes.Proxy.proxy(service, request.uri.tail))

      case _ => NotFound(s"Resource not found: $path")
    }
  }

  /** We don't want to redirect for POST requests */

  def catchRelativePost(path: String) = Action.async { implicit request =>

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
