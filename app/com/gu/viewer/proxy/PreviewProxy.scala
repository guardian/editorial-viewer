package com.gu.viewer.proxy

import javax.inject.{Inject, Singleton}
import com.gu.viewer.config.Configuration
import com.gu.viewer.controllers.routes
import com.gu.viewer.logging.Loggable
import com.gu.viewer.views.html
import play.api.libs.ws.WSResponse
import play.api.mvc.{Cookie, Cookies, Session}
import play.api.mvc.Results.{Ok, BadGateway, BadRequest, Redirect}

import scala.concurrent.Future

@Singleton
class PreviewProxy @Inject() (proxyClient: Proxy) extends Loggable {

  private val COOKIE_PREVIEW_SESSION = "PLAY_SESSION"
  private val COOKIE_PREVIEW_AUTH = "GU_PV_AUTH"

  private val SESSION_KEY_PREVIEW_SESSION = "preview-session"
  private val SESSION_KEY_PREVIEW_AUTH = "preview-auth"
  private val SESSION_KEY_RETURN_URL = "preview-auth-return-url"


  val previewLoginUrl = s"http://${Configuration.previewHost}/login"


  def loginCallbackUrl(isSecure: Boolean, host: String) = {
    val protocol = if (isSecure) "https" else "http"
    s"$protocol://$host${routes.Proxy.previewAuthCallback()}"
  }

  /**
   * Transform proxy server relative URI to viewer URI.
   */
  def proxyUriToViewerUri(uri: String) = {
    val proxyUri = """^\/proxy(\/.+)$""".r

    uri match {
      case proxyUri(path) => Some(path)
      case _ => None
    }
  }

  def doPreviewAuth(isSecure: Boolean, host: String, requestUri: String, session: Session) = {
    val proxyRequestUrl = previewLoginUrl

    log.info(s"Proxy Preview auth to: $proxyRequestUrl")

    def handleResponse(response: WSResponse) = {

      // store new preview session from response
      val cookies = Cookies.fromSetCookieHeader(response.header("Set-Cookie"))
      val previewSessionOpt = cookies.get(COOKIE_PREVIEW_SESSION).map( c => SESSION_KEY_PREVIEW_SESSION -> c.value )

      val loc = response.header("Location").get

      previewSessionOpt match {
        case Some(previewSession) => {
          val returnUrl = SESSION_KEY_RETURN_URL -> proxyUriToViewerUri(requestUri).getOrElse(requestUri)
          Ok(html.loginRedirect(loc))
            .withSession(session - SESSION_KEY_PREVIEW_SESSION - SESSION_KEY_PREVIEW_AUTH + previewSession + returnUrl)
        }

        case None => badGatewayResponse("Unexpected response from preview login request", response)
      }
    }

    proxyClient.post(proxyRequestUrl, queryString = Seq("redirect-url" -> loginCallbackUrl(isSecure, host))) {
      case response if response.status == 303 => Future.successful(handleResponse(response))
      // TODO should we handle non redirect responses here?
    }
  }


  def doPreviewProxy(isSecure: Boolean, host: String, requestUri: String, url: String, session: Session, previewSession: String, auth: String) = {
    log.info(s"Proxy to preview: $url")

    val cookies = Seq(COOKIE_PREVIEW_SESSION -> previewSession, COOKIE_PREVIEW_AUTH -> auth).map(c => Cookie(c._1, c._2))

    /* TODO handle redirects with proxy
        case (status, Some(otherLocation)) => {
          log.warn(s"Proxied response for $url is $status redirect to: $otherLocation")
          Future.successful(Status(status).withHeaders("Location" -> otherLocation))
        }
    */

    def isLoginRedirect(response: WSResponse) = {
      response.status == 303 &&
        response.header("Location").exists(l => l == previewLoginUrl || l == "/login")
    }

    proxyClient.get(url, cookies = cookies) {
      case response if isLoginRedirect(response) => doPreviewAuth(isSecure, host, requestUri, session)
    }

  }


  def proxy(isSecure: Boolean, host: String, requestUri: String, url: String, session: Session) = {
      session.get(SESSION_KEY_PREVIEW_SESSION) -> session.get(SESSION_KEY_PREVIEW_AUTH) match {
        case (Some(previewSession), Some(auth)) => doPreviewProxy(isSecure, host, requestUri, url, session, previewSession, auth)
        case _ => doPreviewAuth(isSecure, host, requestUri, session)
      }
  }



  /**
   * Preview Authentication callback.
   *
   * Proxy all request params and Preview session cookie to Preview authentication callback.
   * Store response cookies into Viewer's play session.
   */
  def previewAuthCallback(host: String, isSecure: Boolean, requestQueryString: Map[String, Seq[String]], session: Session) = {

    val queryParams = requestQueryString.flatMap( q => q._2.map { v => q._1 -> v } ).toSeq :+ ("redirect-url" -> loginCallbackUrl(isSecure, host))

    def handleResponse(response: WSResponse) = {
      val newCookies: Map[String, Cookie] = response.allHeaders.get("Set-Cookie") match {
        case Some(setCookieHeader) => setCookieHeader
          .flatMap { h => Cookies.fromSetCookieHeader(Some(h)) }
          .groupBy(_.name)
          .mapValues(_.head)

        case None => Map.empty
      }

      val responseCookies = Cookies.fromCookieHeader(response.header("Cookie"))

      val sessionOpt = newCookies.get(COOKIE_PREVIEW_SESSION)
        .orElse(responseCookies.get(COOKIE_PREVIEW_SESSION))
        .map(c => SESSION_KEY_PREVIEW_SESSION -> c.value)

      val authOpt = newCookies.get(COOKIE_PREVIEW_AUTH).map(c => SESSION_KEY_PREVIEW_AUTH -> c.value)

      (sessionOpt, authOpt) match {
        case (Some(sessionValue), Some(authValue)) => {
          val returnUrl = session.get(SESSION_KEY_RETURN_URL).getOrElse("/proxy/preview/uk")
          Redirect(returnUrl)
            .withSession(session - SESSION_KEY_RETURN_URL + sessionValue + authValue)
        }
        case (None, None) => badGatewayResponse("Bad response from preview auth callback", response)
        case (None, _) => badGatewayResponse("Preview Session cookie not returned", response)
        case (_, None) => badGatewayResponse("Preview Auth cookie not returned", response)
      }
    }

    session.get(SESSION_KEY_PREVIEW_SESSION) match {
      case None => Future.successful(BadRequest("Preview session not established"))

      case Some(previewSession) => {
        val proxyUrl = s"http://${Configuration.previewHost}/oauth2callback"
        log.info(s"Proxy preview auth callback to: $proxyUrl")

        val cookies = Seq(Cookie(COOKIE_PREVIEW_SESSION, previewSession))

        proxyClient.get(proxyUrl, queryString = queryParams, cookies = cookies) {
          case r => Future.successful(handleResponse(r))
        }
      }
    }
  }



  private def badGatewayResponse(msg: String, response: WSResponse) = {
    log.warn(s"$msg: ${response.toString} ${response.allHeaders} ${response.body}")
    BadGateway(msg)
  }


}



