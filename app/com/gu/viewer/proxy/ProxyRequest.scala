package com.gu.viewer.proxy

import play.api.mvc.RequestHeader


sealed trait ProxyRequest

object ProxyRequest {
  def apply(service: String, servicePath: String, request: RequestHeader, body: Option[Map[String, Seq[String]]] = None): ProxyRequest = {
    val queryString = if (request.rawQueryString.nonEmpty) s"?${request.rawQueryString}" else ""

    service match {
      case "live" => LiveProxyRequest(servicePath + queryString, body)
      case "preview" => PreviewProxyRequest(servicePath + queryString, request, body)
      case _ => UnknownProxyRequest
    }
  }
}

case class LiveProxyRequest(servicePath: String, body: Option[Map[String, Seq[String]]] = None) extends ProxyRequest

case class PreviewProxyRequest(servicePath: String,
                               requestHost: String,
                               requestUri: String,
                               requestQueryString: Map[String, Seq[String]],
                               session: PreviewSession,
                               body: Option[Map[String, Seq[String]]] = None
                              ) extends ProxyRequest

object PreviewProxyRequest {
  def apply(servicePath: String, request: RequestHeader, body: Option[Map[String, Seq[String]]]): PreviewProxyRequest =
    PreviewProxyRequest(servicePath, request.host, request.uri, request.queryString, PreviewSession(request.session), body)

  def authCallbackRequest(request: RequestHeader) =
    PreviewProxyRequest("/oauth2callback", request, None)
}

case object UnknownProxyRequest extends ProxyRequest {
  val isSecure = false
}
