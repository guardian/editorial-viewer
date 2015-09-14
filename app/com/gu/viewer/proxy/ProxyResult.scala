package com.gu.viewer.proxy

import com.gu.viewer.logging.Loggable
import com.gu.viewer.views.html
import play.api.http.HeaderNames._
import play.api.http.MimeTypes._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.Results._
import play.api.mvc.Result
import scala.concurrent.Future


sealed trait ProxyResult

case class ProxyResultWithBody(response: ProxyResponse) extends ProxyResult

case class RedirectProxyResult(location: String) extends ProxyResult

case class RedirectProxyResultWithSession(location: String, session: PreviewSession) extends ProxyResult

case class PreviewAuthRedirectProxyResult(location: String, session: PreviewSession) extends ProxyResult


object ProxyResult extends Loggable {

  /**
   * Convert ProxyResult to a Play Result
   */
  def asResult(proxyResult: ProxyResult): Result = proxyResult match {

    // Stream body result
    case ProxyResultWithBody(response) => {
      val resultHeaders = Seq(
        response.header(CONTENT_LENGTH).map(CONTENT_LENGTH -> _)
      ).flatten

      Status(response.status)
        .stream(response.body)
        .withHeaders(resultHeaders: _*)
        .as(response.header(CONTENT_TYPE).getOrElse(TEXT))
    }

    case RedirectProxyResult(location) =>
      Redirect(location)

    case PreviewAuthRedirectProxyResult(location, session) => {
      Ok(html.loginRedirect(location))
        .withSession(session.asPlaySession)
    }

    case RedirectProxyResultWithSession(location, session) =>
      Redirect(location)
        .withSession(session.asPlaySession)

  }


  def resultFrom(proxyResult: Future[ProxyResult]) = proxyResult
    .map(asResult)
    .recover {
      case err @ ProxyError(message, Some(response)) => {
        log.warn(s"[Bad Gateway] $message: ${response.toString} ${response.bodyAsString}", err)
        BadGateway(message)
      }
      case ProxyError(message, None) => {
        log.warn(s"[Bad Gateway] $message")
        BadGateway(message)
      }
    }

}