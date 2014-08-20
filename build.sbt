organization := "heroku"

name := "kafka-tails"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.10.4"

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "org.apache.kafka" %% "kafka"  %"0.8.1" exclude("log4j", "log4j") exclude("org.slf4j","slf4j-log4j12")

libraryDependencies += "org.slf4j" % "log4j-over-slf4j" % "1.7.7"

val root = Project("kafka-tails", file(".")).configs(IntegrationTest).settings(Defaults.itSettings:_*)
