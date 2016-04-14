package com.gu.viewer.controllers

import com.gu.viewer.logging.Loggable
import play.api._
import play.api.mvc._
import com.gu.viewer.config.Configuration
import com.gu.viewer.views.html

class Application extends Controller with Loggable {

  Loggable.init()

  def index = Action {
    Redirect("/live/uk")
  }

  def previewViewer = (path: String) => {
    viewer("preview", path, "preview")
  }

  def liveViewer = (path: String) => {
    viewer("live", path, "live")
  }

  def viewer(target: String, path: String, previewEnv: String) = Action { implicit request =>
    val protocol = if (request.secure) "https" else "http"
    val viewerHost = target match {
      case "preview" => Configuration.previewHost
      case _ => Configuration.liveHost
    }
    val actualUrl = s"$protocol://$viewerHost/$path"
    val viewerUrl = routes.Proxy.proxy(target, path).path()
    val proxyBase = routes.Proxy.proxy(target, "").absoluteURL()
    val composerUrl = Configuration.composerReturn + "/" + path

    Ok(html.viewer(viewerUrl, actualUrl, previewEnv, composerUrl, proxyBase, path))
  }

}
