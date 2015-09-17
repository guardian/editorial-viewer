package com.gu.viewer.proxy


case class ProxyError(message: String, response: Option[ProxyResponse]) extends RuntimeException(message)
