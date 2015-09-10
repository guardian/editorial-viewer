package com.gu.viewer.proxy

import javax.inject.Inject
import com.gu.viewer.config.Configuration
import com.gu.viewer.logging.Loggable


class LiveProxy @Inject() (proxyClient: Proxy) extends Loggable {

  val serviceHost = Configuration.liveHost

  def proxy(request: LiveProxyRequest) = {
    val url = s"${request.protocol}://$serviceHost/${request.servicePath}"
    log.info(s"Live Proxy to: $url")
    // TODO rewrite redirects to proxied URLS
    proxyClient.get(url, followRedirects = true)()
  }

}
