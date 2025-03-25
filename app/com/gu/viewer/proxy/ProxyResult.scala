package com.gu.viewer.proxy

import com.gu.viewer.logging.Loggable
import com.gu.viewer.views.html
import play.api.http.CookiesConfiguration
import play.api.http.HeaderNames._
import play.api.http.MimeTypes._
import play.api.mvc.Results._
import play.api.mvc.{Cookie, CookieHeaderEncoding, Cookies, Result}

import scala.concurrent.{ExecutionContext, Future}


sealed trait ProxyResult

case class ProxyResultWithBody(response: ProxyResponse, CookiesToSet: List[Cookie] = List.empty) extends ProxyResult

case class RedirectProxyResult(location: String) extends ProxyResult

case class RedirectProxyResultWithSession(location: String, session: PreviewSession) extends ProxyResult

case class PreviewAuthRedirectProxyResult(location: String, session: PreviewSession) extends ProxyResult


object ProxyResult extends Loggable with CookieHeaderEncoding {
  protected def config: CookiesConfiguration = CookiesConfiguration(strict = true)

  /**
   * Convert ProxyResult to a Play Result
   */
  private def asResult(proxyResult: ProxyResult): Result = proxyResult match {
    // Stream body result
    case ProxyResultWithBody(response, cookiesToSet) => {
      val resultHeaders = Seq(
        response.header(CONTENT_LENGTH).map(CONTENT_LENGTH -> _),
        Some(SET_COOKIE -> encodeSetCookieHeader(cookiesToSet))
      ).flatten

      Status(response.status)
        .chunked(response.bodyAsSource)
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


  def resultFrom(proxyResult: Future[ProxyResult])(implicit ec: ExecutionContext) = proxyResult
    .map(asResult)
    .recover {
      case err @ ProxyError(message, Some(response)) => {
        log.warn(s"[Bad Gateway] $message: ${response.toString} ${response.bodyAsString}", err)
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