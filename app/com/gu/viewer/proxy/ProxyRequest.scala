package com.gu.viewer.proxy

import play.api.mvc.{Session, RequestHeader}


sealed trait ProxyRequest {
  val isSecure: Boolean
  val protocol: String = if (isSecure) "https" else "http"
}

object ProxyRequest {
  def apply(service: String, servicePath: String, request: RequestHeader): ProxyRequest = service match {
    case "live" => LiveProxyRequest(request.secure, servicePath)
    case "preview" => PreviewProxyRequest(servicePath, request)
    case _ => UnknownProxyRequest
  }
}

case class LiveProxyRequest(isSecure: Boolean, servicePath: String) extends ProxyRequest

case class PreviewProxyRequest(isSecure: Boolean,
                               servicePath: String,
                               requestHost: String,
                               requestUri: String,
                               requestQueryString: Map[String, Seq[String]],
                               session: PreviewSession) extends ProxyRequest

object PreviewProxyRequest {
  def apply(servicePath: String, request: RequestHeader): PreviewProxyRequest =
    PreviewProxyRequest(request.secure, servicePath, request.host, request.uri, request.queryString, PreviewSession(request.session))

  def authCallbackRequest(request: RequestHeader) =
    PreviewProxyRequest("/oauth2callback", request)
}

case object UnknownProxyRequest extends ProxyRequest {
  val isSecure = false
}
