package com.gu.viewer.controllers

import com.gu.viewer.config.Configuration
import com.gu.viewer.proxy.{Proxy => NewProxy, PreviewProxy}
import javax.inject.Inject
import com.gu.viewer.logging.Loggable
import play.api.mvc.{Controller, Action}


class Proxy @Inject() (newProxy: NewProxy, previewProxy: PreviewProxy) extends Controller with Loggable {

  def proxy(service: String, path: String) = Action.async { implicit request =>
    val protocol = if (request.secure) "https" else "http"
    val serviceHost = service match {
      case "preview" => Configuration.previewHost
      case _ => Configuration.liveHost
    }
    val url = s"$protocol://$serviceHost/$path"

    if (service == "preview") {
      previewProxy.proxy(request.secure, request.host, request.uri, url, request.session)

    } else {
      // live
      log.info(s"Proxy to: $url")

      // TODO rewrite redirects to proxied URLS
      newProxy.get(url, followRedirects = true)()
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
