package com.gu.viewer.controllers

import com.amazonaws.services.simpleemail.model._
import com.gu.viewer.logging.Loggable
import play.api._
import play.api.mvc._
import com.gu.viewer.config.Configuration
import com.gu.viewer.views.html
import com.gu.viewer.aws.AWS
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

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

  def viewer(target: String, path: String, previewEnv: String) = CORSWrapper { Action { implicit request =>
      val protocol = if (request.secure) "https" else "http"
      val viewerHost = target match {
        case "preview" => Configuration.previewHost
        case _ => Configuration.liveHost
      }
      val actualUrl = s"$protocol://$viewerHost/$path"
      val viewerUrl = routes.Proxy.proxy(target, path).absoluteURL()
      val proxyBase = routes.Proxy.proxy(target, "").absoluteURL()
      val composerUrl = Configuration.composerReturn + "/" + path

      Ok(html.viewer(viewerUrl, actualUrl, previewEnv, composerUrl, proxyBase, path))
    }
  }
}

// Wrapper that enables http verisons of viewer to call https versions, required for emailing with panda credentials
case class CORSWrapper[A](action: Action[A]) extends Action[A] {
  def apply(request: Request[A]): Future[Result] = {
    val corsHeader: Seq[(String, String)] = Seq(
      ("Access-Control-Allow-Origin", "https://viewer." + Configuration.pandaDomain),
      ("Access-Control-Allow-Credentials", "true")
    )
    action(request).map(_.withHeaders(corsHeader : _*))
  }

  lazy val parser = action.parser
}
