package com.gu.viewer.controllers

import com.gu.viewer.config.Configuration
import com.gu.viewer.logging.Loggable
import com.gu.viewer.proxy._
import javax.inject.Inject
import play.api.mvc.{Controller, Action}
import scala.concurrent.Future


class Proxy @Inject() (previewProxy: PreviewProxy, liveProxy: LiveProxy) extends Controller with Loggable {

  def proxy(service: String, path: String) = Action.async { implicit request =>
    val protocol = if (request.secure) "https" else "http"
    val serviceHost = service match {
      case "preview" => Configuration.previewHost
      case _ => Configuration.liveHost
    }
    val url = s"$protocol://$serviceHost/$path"

    ProxyRequest(service, path, request) match {
      case r: PreviewProxyRequest => previewProxy.proxy(request.secure, request.host, request.uri, url, request.session)
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
  def previewAuthCallback = Action.async { request =>
    previewProxy.previewAuthCallback(request.host, request.secure, request.queryString, request.session)
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

}
