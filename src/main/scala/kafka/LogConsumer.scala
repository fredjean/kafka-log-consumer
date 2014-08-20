package kafka

import java.util.Properties
import kafka.serializer._
import kafka.utils._
import kafka.message._
import kafka.consumer._
import kafka.utils.Logging
import scala.collection.JavaConverters._

case class LogTopicConsumer(topic: String,
	groupId: String,
	zookeeperConnect: String = "localhost:2181",
	readFromStartOfStream: Boolean = true
) extends Logging {
	val props = new Properties()
	props.put("group.id", groupId)
	props.put("zookeeper.connect", zookeeperConnect)
	props.put("auto.offset.reset", if (readFromStartOfStream) "smallest" else "largest")

	val config = new ConsumerConfig(props)
	val consumer = Consumer.create(config)

	val filterSpec = new Whitelist(topic)

	val streams = consumer.createMessageStreamsByFilter(filterSpec, 1, new DefaultDecoder(), new DefaultDecoder())
	val stream = streams(0)

	def read(write: (Array[Byte]) => Unit) = {
		for(messageAndTopic <- stream) {
			write(messageAndTopic.message)
		println("We got a message")	
		}
		println("We stopped getting messages.")
	}

	def close() {
		consumer.shutdown()
	}
}
