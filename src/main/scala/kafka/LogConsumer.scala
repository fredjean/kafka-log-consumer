package kafka

import java.util.{Collections,Properties, UUID}
import kafka.common._
import kafka.javaapi._
import kafka.javaapi.consumer.SimpleConsumer
import scala.collection._
import scala.collection.JavaConverters._

case class LogConsumer(token: String) {
  var replicaBrokers = List[String]()

  private def findLeader(seedBrokers: List[String], port: Int, topic: String, partition: Int): PartitionMetadata = {
    var returnMetadata: PartitionMetadata = null

    for(broker <- seedBrokers) {
      val consumer = new SimpleConsumer(broker, port, 100000, 64 * 1024, "leaderLookup")
      val topics = Collections.singletonList(topic)

      val req = new TopicMetadataRequest(topics);
      val resp = consumer.send(req)
      
      val metaData = resp.topicsMetadata

      for (item <- metaData.asScala) {
        returnMetadata = item.partitionsMetadata.asScala.find( part => part.partitionId == partition).get
      }

      if (consumer != null) { consumer.close() }

      if (returnMetadata != null) {
        for (replica <- returnMetadata.replicas.asScala) {
          replicaBrokers = replicaBrokers :+ replica.host
        }
      }
    }

    returnMetadata
  }
}

object LogConsumer {
  def getLastOffset(consumer: SimpleConsumer, topic: String, partition:Int, whichTime: Long, clientName: String): Long = {
    val topicAndPartition = new TopicAndPartition(topic, partition)
    val requestInfo = Map[TopicAndPartition, PartitionOffsetRequestInfo]()
    val request = new kafka.javaapi.OffsetRequest(requestInfo, kafka.api.OffsetRequest.CurrentVersion, clientName)
    val response = consumer.getOffsetsBefore(request)

    if (response.hasError) {
      // log failure...
      return 0
    }

    val offsets = response.offsets(topic,partition)
    return offsets(0)
  }
}
