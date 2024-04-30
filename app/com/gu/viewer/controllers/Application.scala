package com.gu.viewer.controllers

import com.gu.viewer.config.AppConfig
import com.gu.viewer.logging.Loggable
import com.gu.viewer.views.html
import play.api.mvc._
import play.filters.csrf.CSRF

class Application(val controllerComponents: ControllerComponents, config: AppConfig)
  extends BaseController with Loggable {

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
      case "preview" => config.previewHost
      case _ => config.liveHost
    }
    val actualUrl = s"$protocol://$viewerHost/$path"
    // TODO rename viewerUrl to iframeSrc and ideally eliminate the proxy all together for preview (following https://github.com/guardian/frontend/pull/27012)
    val viewerUrl = routes.Proxy.proxy(target, path).path()
    val proxyBase = routes.Proxy.proxy(target, "").absoluteURL()
    val composerUrl = config.composerReturn + "/" + path

    Ok(html.viewer(viewerUrl, actualUrl, previewEnv, composerUrl, proxyBase, path, config.googleTrackingId, CSRF.getToken))
  }
}
