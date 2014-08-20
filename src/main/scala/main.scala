object Main extends App {
	val logTopicConsumer = kafka.LogTopicConsumer(topic = "log-shuttle",groupId = "log-reader")

	val loadAverages = scala.collection.mutable.Map[String, Float]()
	val sourceMatcher = "source=(.+)".r 
	val avgMatcher = "sample#load_avg_1m=(.+)".r
	
	def writer(binaryMessage: Array[Byte]): Unit = {
		val message = new String(binaryMessage).split(" ")
		if (message.length > 10) {
		if (message(5) == "dynoload") {
			val source = message(7) match {
				case sourceMatcher(src) => src
				case _ => println(message(7))
			}
			
			val a1mAverage = message(9) match {
				case avgMatcher(avg) => avg.toFloat
				case _ => println(message(9))
				
			}
			
		}
	}
}


	logTopicConsumer.read(writer)
}
