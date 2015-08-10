package com.gu.viewer.controllers

import play.api._
import play.api.mvc._
import com.gu.viewer.views.html

class Application extends Controller {

  def index = Action {
    Ok(html.index("Your new application is ready."))
  }

}
