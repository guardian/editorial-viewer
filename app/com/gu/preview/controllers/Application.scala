package com.gu.preview.controllers

import play.api._
import play.api.mvc._
import com.gu.preview.views.html

class Application extends Controller {

  def index = Action {
    Ok(html.index("Your new application is ready."))
  }

}
