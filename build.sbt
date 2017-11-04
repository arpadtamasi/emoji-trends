name := "twitter-trends"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.12.4"


libraryDependencies ++= Seq(
  // Twitter stream
  "org.twitter4j" % "twitter4j-stream" % "4.0.6",

  // Emoji
  "com.vdurmont" % "emoji-java" % "4.0.0",

  // Logging
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",

  // Akka streams and log
  "com.typesafe.akka" %% "akka-stream" % "2.5.6",
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.6",

  // Redis for the counters
  "net.debasishg" %% "redisclient" % "3.4",

  // Dependency injection
  "net.codingwell" %% "scala-guice" % "4.1.0"
)
