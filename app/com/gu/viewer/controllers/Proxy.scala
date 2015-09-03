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

  val log = Logger.logger

  val queryRegex = "(\\?.*redirect_uri=)([^&]+)".r


  def proxy(service: String, path: String) = Action.async { request =>
    val protocol = if (request.secure) "https" else "http"
    val serviceHost = service match {
      case "preview" => Configuration.previewHost
      case _ => Configuration.liveHost
    }
    val url = s"$protocol://$serviceHost/$path"


    def doPreviewAuth() = {
      val loginCallbackUrl = s"$protocol://${request.host}${routes.Proxy.previewAuthCallback()}"

      ws.url(s"http://${Configuration.previewHost}/login")
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
              val previewSession = cookies.get("PLAY_SESSION").map( c => "preview-session" -> c.value )

              previewSession match {
                case Some(session) => Redirect(newLocation)
                  .withSession(request.session - "preview-session" - "preview-auth" + session)

                case None => BadGateway("Unexpected response from preview login request")
              }
            }
            case None => BadGateway
          }
      }
    }

    def doPreviewProxy(session: String, auth: String) = {
      log.info("Do preview proxy")
      val cookies = Seq("PLAY_SESSION" -> session, "GU_PV_AUTH" -> auth).map(c => Cookie(c._1, c._2))
      val proxyRequest: WSRequest = ws.url(url)
        .withFollowRedirects(follow = false)
        .withHeaders("Cookie" -> Cookies.encodeCookieHeader(cookies))

      proxyRequest.get().map { response =>

        Ok(response.body)
          .withSession(request.session)

          .as(response.header("Content-Type").getOrElse("text/plain"))
      }
    }

    def doProxy() = {
      log.info("Do normal proxy")
      val proxyRequest: WSRequest = ws.url(url)

      proxyRequest.get().map { response =>
        Ok(response.body)
          .as(response.header("Content-Type").getOrElse("text/plain"))
      }
    }

    if (service == "preview") {
      // Cold start
      //  - no preview-session in cookie
      //  - POST to preview/login
      //  - follow redirects for google login
      //  - auth callback redirects back to original request

      //  - 303 response from preview (expired)
      //  - POST to preview/login
      //  - follow redirects for google login
      //  - auth callback redirects back to original request

      request.session.get("preview-session") -> request.session.get("preview-auth") match {
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

      val sessionOpt = newCookies.get("PLAY_SESSION")
        .orElse(responseCookies.get("PLAY_SESSION"))
        .map(c => "preview-session" -> c.value)

      val authOpt = newCookies.get("GU_PV_AUTH").map(c => "preview-auth" -> c.value)


      (sessionOpt, authOpt) match {
        case (Some(sessionValue), Some(authValue)) => Redirect("/proxy/preview/uk")
          .withSession(request.session + sessionValue + authValue)
        case (None, None) => BadGateway("Bad response from preview auth callback")
        case (None, _) => BadGateway("Preview Session cookie not returned")
        case (_, None) => BadGateway("Preview Auth cookie not returned")
      }
    }

    request.session.get("preview-session") match {
      case None => Future.successful(BadRequest("Preview session not established"))

      case Some(session) => ws.url(s"http://${Configuration.previewHost}/oauth2callback")
        .withQueryString(queryParams.toSeq: _*)
        .withHeaders("Cookie" -> Cookies.encodeCookieHeader( Seq( Cookie("PLAY_SESSION", session, path = "/", httpOnly = true ) ) ) )
        .withFollowRedirects(follow = false)
        .withRequestTimeout(30000)
        .get()
        .map(handleResponse)
    }
  }

}
