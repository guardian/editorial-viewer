package com.gu.viewer.proxy

import javax.inject.{Inject, Singleton}
import com.gu.viewer.config.Configuration
import com.gu.viewer.controllers.routes
import com.gu.viewer.logging.Loggable
import com.gu.viewer.views.html
import play.api.libs.ws.WSResponse
import play.api.mvc.Results.{Ok, BadGateway, BadRequest, Redirect}

import scala.concurrent.Future

@Singleton
class PreviewProxy @Inject() (proxyClient: Proxy) extends Loggable {

  val serviceHost = Configuration.previewHost
  val previewLoginUrl = s"http://$serviceHost/login"


  def loginCallbackUrl(request: PreviewProxyRequest) =
    s"${request.protocol}://${request.requestHost}${routes.Proxy.previewAuthCallback()}"


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

  def doPreviewAuth(request: PreviewProxyRequest) = {
    val proxyRequestUrl = previewLoginUrl

    log.info(s"Proxy Preview auth to: $proxyRequestUrl")

    def handleResponse(response: WSResponse) = {

      val returnUrl = proxyUriToViewerUri(request.requestUri).getOrElse(request.requestUri)

      // Store new preview session from response
      //  - should we also remove auth cookie from session to ensure its recreated?
      val session = PreviewSession.fromResponseHeaders(response)
        .withPlaySessionFrom(request.session)
        .withReturnUrl(Some(returnUrl))

      val loc = response.header("Location").get

      session.sessionCookie match {
        case Some(_) =>
          Ok(html.loginRedirect(loc))
            .withSession(session.asPlaySession)

        case None => badGatewayResponse("Unexpected response from preview login request", response)
      }
    }

    proxyClient.post(proxyRequestUrl, queryString = Seq("redirect-url" -> loginCallbackUrl(request))) {
      case response if response.status == 303 => Future.successful(handleResponse(response))
      // TODO should we handle non redirect responses here?
    }
  }


  def doPreviewProxy(request: PreviewProxyRequest) = {

    val url = s"${request.protocol}://$serviceHost/${request.servicePath}"
    log.info(s"Proxy to preview: $url")

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

    val cookies = request.session.asCookies

    proxyClient.get(url, cookies = cookies) {
      case response if isLoginRedirect(response) => doPreviewAuth(request)
    }

  }


  def proxy(request: PreviewProxyRequest) = {
      request.session.sessionCookie -> request.session.authCookie match {
        case (Some(_), Some(_)) => doPreviewProxy(request)
        case _ => doPreviewAuth(request)
      }
  }



  /**
   * Preview Authentication callback.
   *
   * Proxy all request params and Preview session cookie to Preview authentication callback.
   * Store response cookies into Viewer's play session.
   */
  def previewAuthCallback(request: PreviewProxyRequest) = {

    val redirectUrlParam = "redirect-url" -> loginCallbackUrl(request)
    val queryParams = request.requestQueryString.mapValues(_.head).toSeq :+ redirectUrlParam

    def handleResponse(response: WSResponse) = {

      val session = PreviewSession.fromResponseHeaders(response)
        .withPlaySessionFrom(request.session)

      (session.sessionCookie, session.authCookie) match {
        case (Some(_), Some(_)) => {
          val returnUrl = request.session.returnUrl.getOrElse("/proxy/preview/uk")
          Redirect(returnUrl)
            .withSession(session.withoutReturnUrl.asPlaySession)
        }
        case (None, None) => badGatewayResponse("Bad response from preview auth callback", response)
        case (None, _) => badGatewayResponse("Preview Session cookie not returned", response)
        case (_, None) => badGatewayResponse("Preview Auth cookie not returned", response)
      }
    }

    request.session.sessionCookie match {
      case None => Future.successful(BadRequest("Preview session not established"))

      case Some(_) => {
        val proxyUrl = s"http://${Configuration.previewHost}/oauth2callback"
        log.info(s"Proxy preview auth callback to: $proxyUrl")

        val cookies = request.session.asCookies

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



