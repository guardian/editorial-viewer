package com.gu.viewer.proxy

import com.gu.viewer.logging.Loggable
import play.api.mvc.{Cookie, Result}

import scala.concurrent.{ExecutionContext, Future}


class LiveProxy(serviceHost: String, proxyClient: ProxyClient)(implicit ec: ExecutionContext) extends Loggable {

  def getDefaultCookies(request: LiveProxyRequest): Seq[Cookie] =  {

    val secureCookie = Cookie(
      name = "https_opt_in",
      value = "true",
      domain = Some("www.theguardian.com")
    )

    request.isSecure match {
      case false => Seq.empty
      case true => Seq(secureCookie)
    }
  }

  def proxy(request: LiveProxyRequest): Future[Result] = ProxyResult.resultFrom {
    val url = s"${request.protocol}://$serviceHost/${request.servicePath}"
    log.info(s"Live Proxy to: $url")

    proxyClient.get(url, cookies = getDefaultCookies(request)).map(ProxyResultWithBody)
  }

  def proxyPost(request: LiveProxyRequest): Future[Result] = ProxyResult.resultFrom {
    val url = s"${request.protocol}://$serviceHost/${request.servicePath}"
    log.info(s"Live POST Proxy to: $url")

    proxyClient.post(destination = url, body = request.body.getOrElse(Map.empty), cookies = getDefaultCookies(request)).map(ProxyResultWithBody)
  }

}
