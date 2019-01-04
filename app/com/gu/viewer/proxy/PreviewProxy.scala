package com.gu.viewer.proxy

import com.gu.viewer.controllers.routes
import com.gu.viewer.logging.Loggable
import play.api.libs.ws.StandaloneWSResponse
import play.api.mvc.Result

import scala.concurrent.{ExecutionContext, Future}

// TODO MRB: can we remove previewHostForceHTTP?

class PreviewProxy(serviceHost: String, previewHostForceHTTP: Boolean, proxyClient: ProxyClient)(implicit ec: ExecutionContext) extends Loggable {

  private val PREVIEW_AUTH_REDIRECT_PARAM = "redirect-url"

  val previewLoginUrl = s"http://$serviceHost/login"


  private def loginCallbackUrl(request: PreviewProxyRequest) =
    s"${request.protocol}://${request.requestHost}${routes.Proxy.previewAuthCallback()}"


  /**
   * Transform proxy server relative URI to viewer URI.
   */
  private def proxyUriToViewerUri(uri: String) = {
    val proxyUri = """^\/proxy(\/.+)$""".r

    uri match {
      case proxyUri(path) => Some(path)
      case _ => None
    }
  }

  private def doPreviewAuth(request: PreviewProxyRequest): Future[ProxyResult] = {
    val proxyRequestUrl = previewLoginUrl

    log.info(s"Proxy Preview auth to: $proxyRequestUrl")

    def handleResponse(response: StandaloneWSResponse) = {

      val returnUrl = proxyUriToViewerUri(request.requestUri).getOrElse(request.requestUri)

      // Store new preview session from response
      //  - should we also remove auth cookie from session to ensure its recreated?
      val session = PreviewSession.fromResponseHeaders(response)
        .withPlaySessionFrom(request.session)
        .withReturnUrl(Some(returnUrl))

      val locHeader = response.header("Location")

      (session.sessionCookie, locHeader) match {
        case (Some(_), Some(location)) => Future.successful {
          PreviewAuthRedirectProxyResult(location, session)
        }

        case (None, _) => error("Unexpected response session from preview login request", response)
        case (_, None) => error("Invalid response from preview login request", response)
      }
    }

    proxyClient.post(proxyRequestUrl, queryString = Seq(PREVIEW_AUTH_REDIRECT_PARAM -> loginCallbackUrl(request))).flatMap {
      case response if response.status == 303 =>
        handleResponse(response)

      case response =>
        error(s"Unexpected response from preview authentication request", response)
    }
  }


  private def doPreviewProxy(request: PreviewProxyRequest) = {

    val protocol: String = previewHostForceHTTP match {
      case true => "http"
      case false => request.protocol
    }

    val url = s"${protocol}://$serviceHost/${request.servicePath}"
    log.info(s"Proxy GET to preview: $url")

    def isLoginRedirect(response: StandaloneWSResponse) = {
      response.status == 303 &&
        response.header("Location").exists(l => l == previewLoginUrl || l == "/login")
    }

    val cookies = request.session.asCookies

    proxyClient.get(url, cookies = cookies).flatMap {
      case response if isLoginRedirect(response) => doPreviewAuth(request)
      case response => Future.successful(ProxyResultWithBody(response))
    }

  }

  private def doPreviewProxyPost(request: PreviewProxyRequest): Future[ProxyResult] = {

    val protocol: String = previewHostForceHTTP match {
      case true => "http"
      case false => request.protocol
    }

    val url = s"${protocol}://$serviceHost/${request.servicePath}"
    log.info(s"Proxy POST to preview: $url")

    def isLoginRedirect(response: StandaloneWSResponse) = {
      response.status == 303 &&
        response.header("Location").exists(l => l == previewLoginUrl || l == "/login")
    }

    val cookies = request.session.asCookies

    proxyClient.post(url, cookies = cookies, body = request.body.getOrElse(Map.empty)).flatMap {
      case response if isLoginRedirect(response) => doPreviewAuth(request)
      case response => Future.successful(ProxyResultWithBody(response))
    }

  }


  /**
   * Entry-point for proxying a request to preview
   */
  def proxy(request: PreviewProxyRequest): Future[Result] = ProxyResult.resultFrom {
    log.info(s"Received proxy request for: ${request.requestUri}")
    request.session.sessionCookie -> request.session.authCookie match {
      case (Some(_), Some(_)) => doPreviewProxy(request)
      case _ => doPreviewAuth(request)
    }
  }

  def proxyPost(request: PreviewProxyRequest): Future[Result] = ProxyResult.resultFrom {
    log.info(s"Received proxy POST request for: ${request.requestUri}")

    request.session.sessionCookie -> request.session.authCookie match {
      case (Some(_), Some(_)) => doPreviewProxyPost(request)
      case _ => doPreviewAuth(request)
    }
  }


  /**
   * Preview Authentication callback.
   *
   * Proxy all request params and Preview session cookie to Preview authentication callback.
   * Store response cookies into Viewer's play session.
   */
  def previewAuthCallback(request: PreviewProxyRequest) = ProxyResult.resultFrom {

    val redirectUrlParam = PREVIEW_AUTH_REDIRECT_PARAM -> loginCallbackUrl(request)
    val queryParams = request.requestQueryString.mapValues(_.head).toSeq :+ redirectUrlParam

    def handleResponse(response: StandaloneWSResponse) = {

      val session = PreviewSession.fromResponseHeaders(response)
        .withPlaySessionFrom(request.session)

      (session.sessionCookie, session.authCookie) match {
        case (Some(_), Some(_)) => Future.successful {
          val returnUrl = request.session.returnUrl.getOrElse("/proxy/preview/uk")
          RedirectProxyResultWithSession(returnUrl, session.withoutReturnUrl)
        }
        case (None, None) => error("Bad response from preview auth callback", response)
        case (None, _) => error("Preview Session cookie not returned", response)
        case (_, None) => error("Preview Auth cookie not returned", response)
      }
    }

    request.session.sessionCookie match {
      case None => error("Preview session not established")

      case Some(_) => {
        val proxyUrl = s"http://$serviceHost/oauth2callback"
        log.info(s"Proxy preview auth callback to: $proxyUrl")

        val cookies = request.session.asCookies

        proxyClient.get(proxyUrl, queryString = queryParams, cookies = cookies).flatMap(handleResponse)
      }
    }
  }

  private def error(msg: String) =
    Future.failed(ProxyError(msg, None))

  private def error(msg: String, response: StandaloneWSResponse) =
    Future.failed(ProxyError(msg, Some(response)))

}



