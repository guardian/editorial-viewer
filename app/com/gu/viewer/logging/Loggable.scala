package com.gu.viewer.logging

trait Loggable {
  val log = play.api.Logger(getClass)
}