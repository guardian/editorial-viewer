package com.gu.viewer.logging

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.{Logger => LogbackLogger}
import com.amazonaws.auth.InstanceProfileCredentialsProvider
import com.gu.logback.appender.kinesis.KinesisAppender
import com.gu.viewer.aws.{AWS, AwsInstanceTags}
import net.logstash.logback.layout.LogstashLayout
import org.slf4j.{LoggerFactory, Logger => SLFLogger}
import play.api.Configuration


trait Loggable {
  val log = play.api.Logger(getClass)
}

object Loggable extends AwsInstanceTags {
  val rootLogger = LoggerFactory.getLogger(SLFLogger.ROOT_LOGGER_NAME).asInstanceOf[LogbackLogger]

  def init(config: Configuration) = {
    for {
      stack <- readTag("Stack")
      app <- readTag("App")
      stage <- readTag("Stage")
      stream <- config.get[Option[String]]("aws.kinesis.logging.streamName")
    } yield {
      val context = rootLogger.getLoggerContext

      val layout = new LogstashLayout()
      layout.setContext(context)
      layout.setCustomFields(s"""{"stack":"$stack","app":"$app","stage":"$stage"}""")
      layout.start()

      val appender = new KinesisAppender[ILoggingEvent]()
      appender.setBufferSize(1000)
      appender.setRegion(AWS.region.getName)
      appender.setStreamName(stream)
      appender.setContext(context)
      appender.setLayout(layout)
      appender.setCredentialsProvider(InstanceProfileCredentialsProvider.getInstance())
      appender.start()

      rootLogger.addAppender(appender)
    }
  }
}
