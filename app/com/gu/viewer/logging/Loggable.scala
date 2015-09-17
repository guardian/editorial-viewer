package com.gu.viewer.logging

import ch.qos.logback.classic.{Logger => LogbackLogger, LoggerContext}
import ch.qos.logback.core.util.Duration
import com.gu.viewer.config.Configuration
import net.logstash.logback.appender.LogstashTcpSocketAppender
import net.logstash.logback.encoder.LogstashEncoder
import org.slf4j.{Logger => SLFLogger, LoggerFactory}
import play.api.{Logger => PlayLogger}
import play.api.libs.json.Json


trait Loggable {
  val log = play.api.Logger(getClass)
}

object Loggable {
  lazy val rootLogger = LoggerFactory.getLogger(SLFLogger.ROOT_LOGGER_NAME).asInstanceOf[LogbackLogger]

  val customFields = Map[String, String](
    "app" -> Configuration.app,
    "stage" -> Configuration.stage,
    "stack" -> Configuration.stack
  )

  private def makeEncoder(context: LoggerContext) = {
    val e = new LogstashEncoder()
    e.setContext(context)
    e.setCustomFields(Json.toJson(customFields).toString())
    e.start()
    e
  }

  private def makeTcpAppender(context: LoggerContext, destination: String) = {
    val a = new LogstashTcpSocketAppender()
    a.setContext(context)
    a.setEncoder(makeEncoder(context))
    a.setKeepAliveDuration(Duration.buildBySeconds(30.0))
    a.addDestination(destination)
    a.start()
    a
  }

  def init() = (Configuration.logstashEnabled, Configuration.logstashDestination) match {
    case (true, Some(dest)) => {
      PlayLogger.info("Initialising logstash tcp appender")
      rootLogger.addAppender(makeTcpAppender(rootLogger.getLoggerContext, dest))
      PlayLogger.info("Logstash tcp appender initialised")
    }
    case _ => PlayLogger.info("Logstash disabled or not configured")
  }
}
