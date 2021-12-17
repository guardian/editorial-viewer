package com.gu.viewer.proxy

import com.gu.viewer.config.AppConfig
import play.api.libs.ws.WSClient
import play.api.mvc.{Cookie, Cookies, DefaultCookieHeaderEncoding}
import play.api.http.HeaderNames.{CONTENT_LENGTH, COOKIE, USER_AGENT}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import play.api.http.CookiesConfiguration
import play.api.http.HttpConfiguration

class ProxyClient(ws: WSClient, config: AppConfig)(implicit ec: ExecutionContext) extends DefaultCookieHeaderEncoding {


  private val TIMEOUT = 10000.millis

  private def proxy(
             method: String,
             destination: String,
             headers: Seq[(String, String)] = Seq.empty,
             cookies: Seq[Cookie] = Seq.empty,
             queryString: Seq[(String, String)] = Seq.empty,
             body: Map[String, Seq[String]] = Map.empty,
             followRedirects: Boolean = false
             )(handler: PartialFunction[ProxyResponse, Future[ProxyResult]] = PartialFunction.empty): Future[ProxyResult] = {

    val cookieHeader = if (cookies.nonEmpty) Some(COOKIE -> encodeCookieHeader(cookies)) else None

    val contentLengthHeader = if (body.nonEmpty) Some(CONTENT_LENGTH -> body.size.toString) else None

    val userAgentHeader = Some(USER_AGENT -> s"gu-viewer ${config.stage}")

    def handleResponse: PartialFunction[ProxyResponse, Future[ProxyResult]] = {
      case response =>
        Future.successful(ProxyResultWithBody(response))
    }

    ws.url(destination)
      .withFollowRedirects(follow = followRedirects)
      .withHttpHeaders(headers ++ contentLengthHeader ++ userAgentHeader ++ cookieHeader: _*)
      .withQueryStringParameters(queryString: _*)
      .withRequestTimeout(TIMEOUT)
      .withBody(body)
      .withMethod(method)
      .stream()
        .map(new ProxyResponse(_))
        .flatMap(handler orElse handleResponse)
  }


  def get(
           destination: String,
           headers: Seq[(String, String)] = Seq.empty,
           cookies: Seq[Cookie] = Seq.empty,
           queryString: Seq[(String, String)] = Seq.empty,
           followRedirects: Boolean = false
           )(handler: PartialFunction[ProxyResponse, Future[ProxyResult]] = PartialFunction.empty) =
    proxy("GET", destination, headers, cookies, queryString, followRedirects = followRedirects)(handler)


  def post(
            destination: String,
            headers: Seq[(String, String)] = Seq.empty,
            cookies: Seq[Cookie] = Seq.empty,
            queryString: Seq[(String, String)] = Seq.empty,
            body: Map[String, Seq[String]] = Map.empty,
            followRedirects: Boolean = false
            )(handler: PartialFunction[ProxyResponse, Future[ProxyResult]] = PartialFunction.empty) =
    proxy("POST", destination, headers, cookies, queryString, body, followRedirects)(handler)

}
