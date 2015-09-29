package com.gu.viewer.logging

import javax.inject.{Singleton, Inject}

import ch.qos.logback.core.Context
import ch.qos.logback.core.util.Duration
import com.google.inject.AbstractModule
import com.gu.viewer.config.Configuration
import net.logstash.logback.appender.LogstashAccessTcpSocketAppender
import net.logstash.logback.encoder.LogstashAccessEncoder
import org.databrary.PlayLogbackAccessApi
import play.api.inject.ApplicationLifecycle
import play.api.libs.json.Json
import play.api.{Logger => PlayLogger}


trait Logger

class LoggerImpl @Inject() (accessLog: PlayLogbackAccessApi, lifecycle: ApplicationLifecycle) extends Logger with Loggable {

  private def makeAccessEncoder(context: Context) = {
    val e = new LogstashAccessEncoder
    e.setContext(context)
    e.start()
    e
  }

  private def makeAccessAppender(context: Context, destination: String) = {
    val a = new LogstashAccessTcpSocketAppender
    a.setContext(context)
    a.setEncoder(makeAccessEncoder(context))
    a.setKeepAliveDuration(Duration.buildBySeconds(30.0))
    a.addDestination(destination)
    a.start()
    a
  }

  private def initAccessLogger(destination: String) = {
    log.info("Initialise access logger")

    accessLog.context.addAppender(makeAccessAppender(accessLog.context, destination))
  }

  // Initialise
  // TODO: use configuration (Configuration.logstashEnabled, Configuration.logstashDestination)
  (true, Some("***REMOVED***")) match {
    case (true, Some(dest)) => initAccessLogger(dest)
    case _ => PlayLogger.info("Logstash disabled or not configured")
  }

}


class LoggerModule extends AbstractModule {
  override def configure() =
    bind(classOf[Logger]).to(classOf[LoggerImpl]).asEagerSingleton()
}
