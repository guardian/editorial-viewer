package com.gu.viewer.controllers

import play.api._
import play.api.mvc._
import com.gu.viewer.config.Configuration
import com.gu.viewer.views.html

class Application extends Controller {

  def index = Action {
    Redirect("/live/uk")
  }

  def previewViewer = (path: String) => {
    viewer("preview", path, "preview")
  }

  def liveViewer = (path: String) => {
    viewer("live", path, "live")
  }

  def viewer(target: String, path: String, previewEnv: String) = Action { request =>
    val protocol = if (request.secure) "https" else "http"
    val viewerHost = target match {
      case "preview" => Configuration.previewHost
      case _ => Configuration.liveHost
    }
    val viewerUrl = s"$protocol://$viewerHost/$path"

    Ok(html.viewer(viewerUrl, previewEnv))
  }

}
