package com.gu.viewer.logging

import ch.qos.logback.classic.{Logger => LogbackLogger}
import ch.qos.logback.classic.spi.ILoggingEvent
import com.amazonaws.auth.InstanceProfileCredentialsProvider
import com.amazonaws.regions.Regions
import com.gu.logback.appender.kinesis.KinesisAppender
import com.gu.viewer.aws.AwsInstanceTags
import com.gu.viewer.config.AppConfig
import net.logstash.logback.layout.LogstashLayout
import org.slf4j.{LoggerFactory, Logger => SLFLogger}

trait Loggable {
  val log = play.api.Logger(getClass)
}

object LogStash {
  private val rootLogger = LoggerFactory.getLogger(SLFLogger.ROOT_LOGGER_NAME).asInstanceOf[LogbackLogger]

  def init(config: AppConfig, tags: AwsInstanceTags, region: Regions): Unit = {
    for {
      stack <- tags.readTag("Stack")
      app <- tags.readTag("App")
      stage <- tags.readTag("Stage")
      stream <- config.logstashKinesisStream
    } {
      val context = rootLogger.getLoggerContext

      val layout = new LogstashLayout()
      layout.setContext(context)
      layout.setCustomFields(s"""{"stack":"$stack","app":"$app","stage":"$stage"}""")
      layout.start()

      val appender = new KinesisAppender[ILoggingEvent]()
      appender.setBufferSize(1000)
      appender.setRegion(region.getName)
      appender.setStreamName(stream)
      appender.setContext(context)
      appender.setLayout(layout)
      appender.setCredentialsProvider(InstanceProfileCredentialsProvider.getInstance())
      appender.start()

      rootLogger.addAppender(appender)
    }
  }
}
