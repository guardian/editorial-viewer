package com.gu.viewer.controllers

import com.amazonaws.services.simpleemail.model._
import com.gu.viewer.logging.Loggable
import play.api._
import play.api.mvc._
import com.gu.viewer.config.Configuration
import com.gu.viewer.aws.AWS
import scala.util.control.NonFatal

class Email extends Controller with Loggable with PanDomainAuthActions {

  Loggable.init()

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
      case NonFatal(e) =>{
        println(e)
        InternalServerError
      }
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
