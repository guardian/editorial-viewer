package com.gu.viewer.proxy

import com.gu.viewer.logging.Loggable
import play.api.http.HeaderNames._
import play.api.http.HttpEntity
import play.api.libs.ws.StandaloneWSResponse
import play.api.mvc.Result
import play.api.mvc.Results._
import com.gu.viewer.views.html

import scala.concurrent.{ExecutionContext, Future}


sealed trait ProxyResult

case class ProxyResultWithBody(response: StandaloneWSResponse) extends ProxyResult

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

      val contentType = response.header(CONTENT_TYPE)
      val contentLength = response.header(CONTENT_LENGTH).map(_.toLong)

      Status(response.status)
        .sendEntity(HttpEntity.Streamed(response.bodyAsSource, contentLength, contentType))
    }

    case PreviewAuthRedirectProxyResult(location, session) => {
      Ok(html.loginRedirect(location))
        .withSession(session.asPlaySession)
    }

    case RedirectProxyResultWithSession(location, session) =>
      Redirect(location)
        .withSession(session.asPlaySession)

  }


  def resultFrom(proxyResult: Future[ProxyResult])(implicit ec: ExecutionContext): Future[Result] = proxyResult
    .map(asResult)
    .recover {
      case err @ ProxyError(message, Some(response)) => {
        log.warn(s"[Bad Gateway] $message: ${response.toString}", err)
        BadGateway(message)
          .withNewSession
      }
      case ProxyError(message, None) => {
        log.warn(s"[Bad Gateway] $message")
        BadGateway(message)
          .withNewSession
      }
    }

}