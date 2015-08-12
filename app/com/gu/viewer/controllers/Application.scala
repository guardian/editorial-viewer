package com.gu.viewer.controllers

import play.api._
import play.api.mvc._
import com.gu.viewer.config.Configuration
import com.gu.viewer.views.html

class Application extends Controller {

  def index = Action {
    Ok(html.index("Your new application is ready."))
  }

  def previewViewer = (path: String) => {
    viewer("preview", path)
  }

  def liveViewer = (path: String) => {
    viewer("live", path)
  }

  def viewer(target: String, path: String) = Action {
    val viewerDomain = target match {
      case "preview" => Configuration.previewHost
      case _ => Configuration.liveHost
    }
    val viewerUrl = s"$viewerDomain/$path"

    Ok(html.viewer(viewerUrl))
  }

}
