package com.gu.viewer.controllers

import com.gu.pandomainauth.PanDomainAuthSettingsRefresher
import com.gu.viewer.config.AppConfig
import com.gu.viewer.logging.Loggable
import play.api.libs.ws.WSClient
import play.api.mvc._
import software.amazon.awssdk.services.ses.SesClient
import software.amazon.awssdk.services.ses.model.{Body, Content, Destination, Message, SendEmailRequest}

import scala.util.control.NonFatal

class Email(
  val controllerComponents: ControllerComponents,
  val wsClient: WSClient,
  emailClient: SesClient,
  val config: AppConfig,
  val panDomainSettings: PanDomainAuthSettingsRefresher
) extends BaseControllerHelpers with Loggable with PanDomainAuthActions {

  def sendEmail(path: String) = APIAuthAction { req =>
    val email = req.user.email
    val from = "editorial.tools.dev@theguardian.com"
    val to = Destination.builder().toAddresses(email).build()
    val shortPath = req.getQueryString("shortPath")

    val message = Message.builder()
      .subject(Content.builder().data(s"Preview URLs for '$path'").build())
      .body(Body.builder().text(Content.builder().data(formatEmail(path, shortPath)).build()).build())
      .build()

    val emailReq = SendEmailRequest.builder().source(from).destination(to).message(message).build()

    try {
      emailClient.sendEmail(emailReq)
      Ok
    } catch {
      case NonFatal(e) =>
        InternalServerError
    }
  }

  private def formatEmail(path: String, shortPath: Option[String]): String = {
    val cleanShortPath = shortPath
      .map(_.trim)
      .filter(_.nonEmpty)
      .map(_.stripPrefix("/"))

    val iosLines = Seq(
      "iOS:",
      s"- Full path (subject to change): https://entry.mobile-apps.guardianapis.com/deeplink/items/$path"
    ) ++ cleanShortPath.map(short => s"- Permalink: https://entry.mobile-apps.guardianapis.com/deeplink/items/$short")

    val androidLines = Seq(
      "Android:",
      s"- Full path (subject to change): https://mobile-preview.guardianapis.com/items/$path"
    ) ++ cleanShortPath.map(short => s"- Permalink: https://mobile-preview.guardianapis.com/items/$short")

    val introLines = Seq(
      "You requested App preview links whilst previewing an article.",
      "Click the appropriate link below from your phone to open the App in preview mode and view the article before it gets published.",
      ""
    )

    (introLines ++ iosLines ++ Seq("") ++ androidLines ++ Seq("", "", "If you were not expecting this email please contact: editorial.tools.dev@theguardian.com")).mkString("\n")
  }
}
