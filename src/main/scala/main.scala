import scala.concurrent.ops._

object Main extends App {
	val logTopicConsumer = kafka.LogTopicConsumer(topic = "log-shuttle",groupId = "log-reader", readFromStartOfStream = false
)

	val loadAverages = scala.collection.mutable.Map[String, Double]()
	val sourceMatcher = " source=(.+?) ".r 
	val avgMatcher = " sample#load_avg_1m=(.+?) ".r
	
	def writer(binaryMessage: Array[Byte]): Unit = {
		val message = new String(binaryMessage)
		if (message.contains("dynoload")) {
			val sourceMatchData = sourceMatcher.findAllIn(message).matchData
			var source = ""
			if (sourceMatchData.hasNext) {
				source = sourceMatchData.toList(0).group(1)
			}
			
			val avgMatchData = avgMatcher.findAllIn(message).matchData
			var a1mAverage = 0.0
			if (avgMatchData.hasNext) {
				a1mAverage = avgMatchData.toList(0).group(1).toDouble
println(a1mAverage)
			}

			loadAverages(source) = a1mAverage
		}
	}

	spawn {
		try {
			logTopicConsumer.read(writer)
println("We are done...")
		} catch {
			case e:Exception => println("Something is odd..." + e)
		}

	}

	while(true) {
		Thread.sleep(1000)
		println(loadAverages)
	}

}
