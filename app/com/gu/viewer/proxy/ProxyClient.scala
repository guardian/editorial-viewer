package com.gu.viewer.proxy

import play.api.http.HeaderNames.{CONTENT_LENGTH, COOKIE, USER_AGENT}
import play.api.libs.ws.{StandaloneWSResponse, WSClient}
import play.api.mvc.{Cookie, Cookies}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class ProxyClient(stage: String, ws: WSClient) {

  private val TIMEOUT = 10000.millis

  private def proxy(
             method: String,
             destination: String,
             cookies: Seq[Cookie] = Seq.empty,
             queryString: Seq[(String, String)] = Seq.empty,
             body: Map[String, Seq[String]] = Map.empty
             )(implicit ec: ExecutionContext): Future[StandaloneWSResponse] = {

    val cookieHeader = if (cookies.nonEmpty) Some(COOKIE -> Cookies.encodeCookieHeader(cookies)) else None
    val contentLengthHeader = if (body.nonEmpty) Some(CONTENT_LENGTH -> body.size.toString) else None
    val userAgentHeader = USER_AGENT -> s"gu-viewer $stage"
    val headers = Seq(userAgentHeader) ++ cookieHeader ++ contentLengthHeader

    ws.url(destination)
      .withHttpHeaders(headers: _*)
      .withQueryStringParameters(queryString: _*)
      .withRequestTimeout(TIMEOUT)
      .withBody(body)
      .withMethod(method)
      .stream()
  }


  def get(destination: String, cookies: Seq[Cookie] = Seq.empty, queryString: Seq[(String, String)] = Seq.empty)(implicit ec: ExecutionContext): Future[StandaloneWSResponse] =
    proxy("GET", destination, cookies, queryString)


  def post(destination: String, cookies: Seq[Cookie] = Seq.empty, queryString: Seq[(String, String)] = Seq.empty, body: Map[String, Seq[String]] = Map.empty)(implicit ec: ExecutionContext): Future[StandaloneWSResponse] =
    proxy("POST", destination, cookies, queryString, body)

}
