package com.gu.viewer.controllers

import com.gu.viewer.config.Configuration
import com.gu.viewer.logging.Loggable
import com.gu.viewer.views.html
import play.api.mvc._

class Application(configuration: Configuration, override val controllerComponents: ControllerComponents) extends BaseController with Loggable {
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
      case "preview" => configuration.previewHost
      case _ => configuration.liveHost
    }
    val actualUrl = s"$protocol://$viewerHost/$path"
    val viewerUrl = routes.Proxy.proxy(target, path).path()
    val proxyBase = routes.Proxy.proxy(target, "").absoluteURL()
    val composerUrl = configuration.composerReturn + "/" + path

    Ok(html.viewer(viewerUrl, actualUrl, previewEnv, composerUrl, proxyBase, path, configuration.googleTrackingId))
  }
}
