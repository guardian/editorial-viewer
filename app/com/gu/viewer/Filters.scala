package com.gu.viewer

import javax.inject.Inject

import org.databrary.PlayLogbackAccessApi
import play.api.http.HttpFilters

class Filters @Inject() (accessLogger: PlayLogbackAccessApi) extends HttpFilters {

  val filters = Seq(accessLogger.filter)

}
