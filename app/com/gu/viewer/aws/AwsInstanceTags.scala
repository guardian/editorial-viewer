package com.gu.viewer.aws

import software.amazon.awssdk.imds.Ec2MetadataClient
import software.amazon.awssdk.services.ec2.Ec2Client
import software.amazon.awssdk.services.ec2.model.{DescribeTagsRequest, Filter}

import scala.jdk.CollectionConverters._
import scala.util.Using

class AwsInstanceTags(ec2: Ec2Client) {
  lazy val instanceId: Option[String] = Using(Ec2MetadataClient.create()) { metadata =>
    metadata.get("/latest/meta-data/instance-id").asString()
  }.toOption

  def readTag(tagName: String) = {
    instanceId.flatMap { id =>
      val tagsResult = ec2.describeTags(
        DescribeTagsRequest.builder().filters(
          Filter.builder().name("resource-type").values("instance").build(),
          Filter.builder().name("resource-id").values(id).build(),
          Filter.builder().name("key").values(tagName).build()
        ).build()
      )
      tagsResult.tags().asScala.find(_.key() == tagName).map(_.value())
    }
  }
}
