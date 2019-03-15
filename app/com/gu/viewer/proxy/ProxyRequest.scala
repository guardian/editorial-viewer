package com.gu.viewer.proxy

import play.api.mvc.{Session, RequestHeader}


sealed trait ProxyRequest {
  val isSecure: Boolean
  val protocol: String = if (isSecure) "https" else "http"
}

object ProxyRequest {
  def apply(service: String, servicePath: String, request: RequestHeader, body: Option[Map[String, Seq[String]]] = None): ProxyRequest = {
    val queryString = if (request.rawQueryString.nonEmpty) s"?${request.rawQueryString}" else ""

    service match {
      case "live" => LiveProxyRequest(request.secure, servicePath + queryString, body)
      case "preview" => PreviewProxyRequest(servicePath + queryString, request, body)
      case _ => UnknownProxyRequest
    }
  }
}

case class LiveProxyRequest(isSecure: Boolean, servicePath: String, body: Option[Map[String, Seq[String]]] = None) extends ProxyRequest

case class PreviewProxyRequest(isSecure: Boolean,
                               servicePath: String,
                               requestHost: String,
                               requestUri: String,
                               requestQueryString: Map[String, Seq[String]],
                               session: PreviewSession,
                               body: Option[Map[String, Seq[String]]] = None
                              ) extends ProxyRequest

object PreviewProxyRequest {
  def apply(servicePath: String, request: RequestHeader, body: Option[Map[String, Seq[String]]]): PreviewProxyRequest =
    PreviewProxyRequest(request.secure, servicePath, request.host, request.uri, request.queryString, PreviewSession(request.session), body)

  def authCallbackRequest(request: RequestHeader) =
    PreviewProxyRequest("/oauth2callback", request, None)
}

case object UnknownProxyRequest extends ProxyRequest {
  val isSecure = false
}
