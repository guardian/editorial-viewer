package com.gu.viewer.controllers

import com.gu.viewer.config.Configuration
import java.net.URLEncoder
import javax.inject.Inject
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current
import play.api.Logger
import play.api.libs.ws.{WSResponse, WSClient, WSRequest}
import play.api.mvc.{Controller, Cookie, Cookies, Action, Result}
import scala.concurrent.Future


class Proxy @Inject() (ws: WSClient) extends Controller {

  private val COOKIE_PREVIEW_SESSION = "PLAY_SESSION"
  private val COOKIE_PREVIEW_AUTH = "GU_PV_AUTH"

  private val SESSION_KEY_PREVIEW_SESSION = "preview-session"
  private val SESSION_KEY_PREVIEW_AUTH = "preview-auth"

  val log = Logger.logger

  val queryRegex = "(\\?.*redirect_uri=)([^&]+)".r

  val previewLoginUrl = s"http://${Configuration.previewHost}/login"


  def proxy(service: String, path: String) = Action.async { request =>
    val protocol = if (request.secure) "https" else "http"
    val serviceHost = service match {
      case "preview" => Configuration.previewHost
      case _ => Configuration.liveHost
    }
    val url = s"$protocol://$serviceHost/$path"


    def doPreviewAuth() = {
      val loginCallbackUrl = s"$protocol://${request.host}${routes.Proxy.previewAuthCallback()}"
      val proxyRequestUrl = previewLoginUrl

      log.info(s"Proxy Preview auth to: $proxyRequestUrl")
      ws.url(proxyRequestUrl)
        .withFollowRedirects(follow = false)
        .withHeaders("Content-Length" -> "0")
        .post(Map.empty[String, Seq[String]])
        .map { response =>
          log.info("Preview auth Response: " + response.toString + response.allHeaders + response.body)

          response.header("Location") match {
            case Some(loc) => {
              val newLocation = queryRegex.replaceAllIn(loc, m => m.group(1) + URLEncoder.encode(loginCallbackUrl, "utf8"))

              // store new preview session from response
              val cookies = Cookies.fromSetCookieHeader(response.header("Set-Cookie"))
              val previewSessionOpt = cookies.get(COOKIE_PREVIEW_SESSION).map( c => SESSION_KEY_PREVIEW_SESSION -> c.value )

              previewSessionOpt match {
                case Some(session) => Redirect(newLocation)
                  .withSession(request.session - SESSION_KEY_PREVIEW_SESSION - SESSION_KEY_PREVIEW_AUTH + session)

                case None => BadGateway("Unexpected response from preview login request")
              }
            }
            case None => BadGateway
          }
      }
    }


    def doPreviewProxy(session: String, auth: String) = {
      log.info(s"Proxy to preview: $url")

      val cookies = Seq(COOKIE_PREVIEW_SESSION -> session, COOKIE_PREVIEW_AUTH -> auth).map(c => Cookie(c._1, c._2))
      val proxyRequest: WSRequest = ws.url(url)
        .withFollowRedirects(follow = false)
        .withHeaders("Cookie" -> Cookies.encodeCookieHeader(cookies))

      proxyRequest.get().flatMap { response =>
        (response.status, response.header("Location")) match {
          case (303, Some(`previewLoginUrl`)) => doPreviewAuth()
          case _ => Future.successful {
            Ok(response.body)
              .withSession(request.session)

              .as(response.header("Content-Type").getOrElse("text/plain"))
          }
        }

      }
    }


    def doProxy() = {
      log.info(s"Proxy to: $url")

      ws.url(url).get().map { response =>
        Ok(response.body)
          .as(response.header("Content-Type").getOrElse("text/plain"))
      }
    }


    if (service == "preview") {
      request.session.get(SESSION_KEY_PREVIEW_SESSION) -> request.session.get(SESSION_KEY_PREVIEW_AUTH) match {
        case (Some(session), Some(auth)) => doPreviewProxy(session, auth)
        case _ => doPreviewAuth()
      }

    } else {
      // live
      doProxy()
    }

  }


  /**
   * Preview Authentication callback.
   *
   * Proxy all request params and Preview session cookie to Preview authentication callback.
   * Store response cookies into Viewer's play session.
   */
  def previewAuthCallback = Action.async { request =>

    // TODO: redirect to original URL

    val queryParams = request.queryString.flatMap( q => q._2.map { v => q._1 -> v } )

    def handleResponse(response: WSResponse): Result = {
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
        case (Some(sessionValue), Some(authValue)) => Redirect("/proxy/preview/uk")
          .withSession(request.session + sessionValue + authValue)
        case (None, None) => BadGateway("Bad response from preview auth callback")
        case (None, _) => BadGateway("Preview Session cookie not returned")
        case (_, None) => BadGateway("Preview Auth cookie not returned")
      }
    }

    request.session.get(SESSION_KEY_PREVIEW_SESSION) match {
      case None => Future.successful(BadRequest("Preview session not established"))

      case Some(session) => {
        val proxyUrl = s"http://${Configuration.previewHost}/oauth2callback"
        log.info(s"Proxy preview auth callback to: $proxyUrl")

        ws.url(proxyUrl)
          .withQueryString(queryParams.toSeq: _*)
          .withHeaders("Cookie" -> Cookies.encodeCookieHeader( Seq( Cookie(COOKIE_PREVIEW_SESSION, session, path = "/", httpOnly = true ) ) ) )
          .withFollowRedirects(follow = false)
          .withRequestTimeout(30000)
          .get()
          .map(handleResponse)
      }
    }
  }

}
