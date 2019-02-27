package com.gu.viewer.controllers

import com.amazonaws.services.simpleemail.model._
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.gu.pandomainauth.PanDomainAuthSettingsRefresher
import com.gu.viewer.config.AppConfig
import com.gu.viewer.logging.Loggable
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.util.control.NonFatal

class Email(val controllerComponents: ControllerComponents, val wsClient: WSClient,
            emailClient: AmazonSimpleEmailService, val config: AppConfig,
            val panDomainSettings: PanDomainAuthSettingsRefresher)
  extends BaseControllerHelpers with Loggable with PanDomainAuthActions {

  def sendEmail(path: String) = APIAuthAction { req =>
    val email = req.user.email
    var emailList = new java.util.ArrayList[String]()
    emailList.add(email)

    val from = "editorial.tools.dev@theguardian.com"
    val to = new Destination(emailList)

    val message = new Message(
      new Content(s"Preview URLs for '$path'"),
      new Body(new Content(formatEmail(path)))
    )

    val emailReq = new SendEmailRequest(from, to, message)

    try {
      emailClient.sendEmail(emailReq)
      Ok
    } catch {
      case NonFatal(e) =>
        InternalServerError
    }
  }

  private def formatEmail(path: String): String = {
    s"""iOS: https://entry.mobile-apps.guardianapis.com/deeplink/items/$path
      |Android: https://mobile-preview.guardianapis.com/items/$path
      |
      |
      |If you were not expecting this email please contact: digitalcms.dev@guardian.co.uk""".stripMargin
  }
}