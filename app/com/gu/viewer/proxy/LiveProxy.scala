package com.gu.viewer.proxy

import com.gu.viewer.config.AppConfig
import com.gu.viewer.logging.Loggable

import scala.concurrent.ExecutionContext

class LiveProxy(proxyClient: ProxyClient, config: AppConfig)(implicit ec: ExecutionContext) extends Loggable {

  val serviceHost = config.liveHost

  def proxy(request: LiveProxyRequest) = ProxyResult.resultFrom {
    val url = s"https://$serviceHost/${request.servicePath}"
    log.info(s"Live Proxy to: $url")

    proxyClient.get(url)()
  }

  def proxyPost(request: LiveProxyRequest) = ProxyResult.resultFrom {
    val url = s"https://$serviceHost/${request.servicePath}"
    log.info(s"Live POST Proxy to: $url")
    proxyClient.post(destination = url, body = request.body.getOrElse(Map.empty))()
  }

}
