package com.gu.viewer

import javax.inject.Inject

import com.gu.viewer.Logging.RequestLoggingFilter
import play.api.http.HttpFilters


class Filters @Inject() (logger: RequestLoggingFilter) extends HttpFilters {

  val filters = Seq(logger)

}
