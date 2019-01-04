package com.gu.viewer.controllers

import java.util

import akka.actor.ActorSystem
import com.amazonaws.services.simpleemail.model._
import com.gu.viewer.aws.AWS
import com.gu.viewer.config.Configuration
import com.gu.viewer.logging.Loggable
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.util.control.NonFatal

class Email(override val configuration: Configuration,
            override val actorSystem: ActorSystem,
            override val wsClient: WSClient,
            override val controllerComponents: ControllerComponents)

  extends BaseController with Loggable with PanDomainAuthActions {

  def sendEmail(path: String) = {
    APIAuthAction { req =>
      val from = "noreply-viewer@guardian.co.uk"
      val to = new Destination(util.Arrays.asList(req.user.email))

      val message = new Message(
        new Content(s"Preview URLs for '$path'"),
        new Body(new Content(formatEmail(path)))
      )

      val emailReq = new SendEmailRequest(from, to, message)

      try {
        AWS.SESClient.sendEmail(emailReq)
        Ok
      } catch {
        case NonFatal(e) =>
          log.error(s"Error sending preview email for $path to ${req.user.email}", e)
          InternalServerError
      }
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
