package com.gu.viewer.controllers

import play.api._
import play.api.mvc._

class Management extends Controller {
  def healthcheck = Action {
    Ok("Healthcheck is OK")
  }
}
