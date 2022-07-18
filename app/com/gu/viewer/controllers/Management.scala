package com.gu.viewer.controllers

import play.api.mvc._

class Management(val controllerComponents: ControllerComponents) extends BaseController {
  def healthcheck = Action {
    Ok("Healthcheck is OK")
  }

  def disallowRobots = Action {
    Ok("User-agent: *\nDisallow: /\n")
  }
}
