package com.gu.viewer.proxy

import com.gu.viewer.config.AppConfig
import com.gu.viewer.logging.Loggable
import play.api.mvc.Cookie

import scala.concurrent.ExecutionContext

class LiveProxy(proxyClient: ProxyClient, config: AppConfig)(implicit ec: ExecutionContext) extends Loggable {

  val serviceHost = config.liveHost

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

  def proxy(request: LiveProxyRequest) = ProxyResult.resultFrom {
    val url = s"${request.protocol}://$serviceHost/${request.servicePath}"
    log.info(s"Live Proxy to: $url")

    proxyClient.get(url, cookies = getDefaultCookies(request))()
  }

  def proxyPost(request: LiveProxyRequest) = ProxyResult.resultFrom {
    val url = s"${request.protocol}://$serviceHost/${request.servicePath}"
    log.info(s"Live POST Proxy to: $url")
    proxyClient.post(destination = url, body = request.body.getOrElse(Map.empty), cookies = getDefaultCookies(request))()
  }

}
