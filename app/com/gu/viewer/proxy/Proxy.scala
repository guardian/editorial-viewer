package com.gu.viewer.proxy

import javax.inject.{Inject, Singleton}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.ws.{WSResponse, WSClient}
import play.api.mvc.Result
import play.api.mvc.Results.Status

import scala.concurrent.Future

@Singleton
class Proxy @Inject() (ws: WSClient) {

  private val TIMEOUT = 10000

  // TODO Cookies?
  case class ProxyRequest(
                           destination: String,
                           headers: Seq[(String, String)] = Seq.empty,
                           queryString: Seq[(String, String)] = Seq.empty,
                           followRedirects: Boolean = false
                         ) {

    val wsRequest = ws.url(destination)
      .withFollowRedirects(follow = followRedirects)
      .withHeaders(headers: _*)
      .withQueryString(queryString: _*)
      .withRequestTimeout(TIMEOUT)

    def get(handler: PartialFunction[WSResponse, Future[Result]] = PartialFunction.empty) = {
      wsRequest
        .get()
        .flatMap(handler orElse handleResponse)
    }

    def post(data: Map[String, Seq[String]] = Map.empty, handler: PartialFunction[WSResponse, Future[Result]] = PartialFunction.empty) = {
      wsRequest
        .withHeaders("Content-Length" -> "0")
        .post(data)
        .flatMap(handler orElse handleResponse)
    }

    private def handleResponse: PartialFunction[WSResponse, Future[Result]] = {
      case (response) => Future.successful {
        Status(response.status)(response.body)
          .as(response.header("Content-Type").getOrElse("text/plain"))
      }
    }

  }

}