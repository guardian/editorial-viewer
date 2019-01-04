package com.gu.viewer

import com.gu.viewer.logging.Loggable
import play.api.{Application, ApplicationLoader, LoggerConfigurator}

class AppLoader extends ApplicationLoader {
  override def load(context: ApplicationLoader.Context): Application = {
    LoggerConfigurator(context.environment.classLoader)
      .foreach(_.configure(context.environment))

    Loggable.init(context.initialConfiguration)

    new AppComponents(context).application
  }
}
