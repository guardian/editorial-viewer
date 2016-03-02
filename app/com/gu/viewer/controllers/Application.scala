package com.gu.viewer.controllers

import com.amazonaws.services.simpleemail.model._
import com.gu.viewer.logging.Loggable
import play.api._
import play.api.mvc._
import com.gu.viewer.config.Configuration
import com.gu.viewer.views.html
import com.gu.viewer.aws.AWS
import scala.util.control.NonFatal
import com.gu.pandomainauth.model.{Authenticated, AuthenticatedUser}

class Application extends Controller with Loggable with PanDomainAuthActions {

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
    extractAuth(request) match {
      case (Authenticated(AuthenticatedUser(user, _, _, _, _))) => {
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
      case _ => {
        Ok(html.login(Configuration.pandaDomain))
      }
    }
  }

  def sendEmail(path: String) = APIAuthAction { req =>
    val email = req.user.email
    var emailList = new java.util.ArrayList[String]()
    emailList.add(email)

    val from = "noreply-viewer@guardian.co.uk"
    val to = new Destination(emailList)

    val message = new Message(
      new Content(s"Preview URLs for '$path'"),
      new Body(new Content(formatEmail(path)))
    )

    val emailReq = new SendEmailRequest(from, to, message)

    try {
      AWS.emailClient.sendEmail(emailReq)
      Ok
    } catch {
      case NonFatal(e) => InternalServerError
    }
  }

  private def formatEmail(path: String): String = {
    s"""iOS: https://entry.mobile-apps.guardianapis.com/deeplink/items/$path
      |Android: http://preview.mobile-apps.guardianapis.com/items/$path
      |
      |
      |If you were not expecting this email please contact: digitalcms.dev@guardian.co.uk""".stripMargin
  }

}
