package com.gu.viewer.proxy

import play.api.mvc.RequestHeader


sealed trait ProxyRequest {
  val isSecure: Boolean
  val protocol: String = if (isSecure) "https" else "http"
}

object ProxyRequest {
  def apply(service: String, servicePath: String, request: RequestHeader): ProxyRequest = service match {
    case "live" => LiveProxyRequest(request.secure, servicePath)
    case "preview" => PreviewProxyRequest(request.secure, servicePath)
    case _ => UnknownProxyRequest
  }
}

case class LiveProxyRequest(isSecure: Boolean, servicePath: String) extends ProxyRequest
case class PreviewProxyRequest(isSecure: Boolean, servicePath: String) extends ProxyRequest

case object UnknownProxyRequest extends ProxyRequest {
  val isSecure = false
}
