package com.gu.viewer.aws

import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.ec2.{AmazonEC2, AmazonEC2Client}
import com.amazonaws.services.ec2.model.{DescribeTagsRequest, Filter}
import com.amazonaws.services.simpleemail._
import com.amazonaws.util.EC2MetadataUtils

import scala.collection.JavaConverters._

class AwsInstanceTags(ec2: AmazonEC2) {
  lazy val instanceId = Option(EC2MetadataUtils.getInstanceId)

  def readTag(tagName: String) = {
    instanceId.flatMap { id =>
      val tagsResult = ec2.describeTags(
        new DescribeTagsRequest().withFilters(
          new Filter("resource-type").withValues("instance"),
          new Filter("resource-id").withValues(id),
          new Filter("key").withValues(tagName)
        )
      )
      tagsResult.getTags.asScala.find(_.getKey == tagName).map(_.getValue)
    }
  }
}
