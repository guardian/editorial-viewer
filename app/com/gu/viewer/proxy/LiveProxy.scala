package com.gu.viewer.proxy

import com.gu.viewer.config.AppConfig
import com.gu.viewer.logging.Loggable
import play.api.mvc.Cookie

import scala.concurrent.{ExecutionContext, Future}
import java.time.{LocalDateTime, ZoneOffset}

class LiveProxy(proxyClient: ProxyClient, config: AppConfig)(implicit
    ec: ExecutionContext
) extends Loggable {

  val serviceHost = config.liveHost

  private def setCookieListForNonAdvertisingBanner(
      domain: String
  ): List[Cookie] = {

    val expiryTime = LocalDateTime
      .now(ZoneOffset.UTC)
      .plusDays(7)
      .toEpochSecond(ZoneOffset.UTC)
      .toString

    val cookieNames = List(
      "gu_allow_reject_all",
      "gu_hide_support_messaging",
      "gu_user_benefits_expiry"
    )

    cookieNames.map(name =>
      Cookie(
        name = name,
        value = expiryTime,
        domain = Some(domain),
        httpOnly = false
      )
    )
  }

  def proxy(request: LiveProxyRequest) = ProxyResult.resultFrom {
    val url = s"https://$serviceHost/${request.servicePath}"

    proxyClient.get(url, cookies = setCookieListForNonAdvertisingBanner(url)) {
      case response =>
        Future.successful(
          ProxyResultWithBody(
            response = response,
            setCookieListForNonAdvertisingBanner(request.request.host)
          )
        )
    }
  }

  def proxyPost(request: LiveProxyRequest) = ProxyResult.resultFrom {
    val url = s"https://$serviceHost/${request.servicePath}"
    log.info(s"Live POST Proxy to: $url")
    proxyClient.post(destination = url, body = request.body.getOrElse(Map.empty))()
  }
}
