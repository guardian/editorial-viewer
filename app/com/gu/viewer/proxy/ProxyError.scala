package com.gu.viewer.proxy

import play.api.libs.ws.StandaloneWSResponse


case class ProxyError(message: String, response: Option[StandaloneWSResponse]) extends RuntimeException(message)
