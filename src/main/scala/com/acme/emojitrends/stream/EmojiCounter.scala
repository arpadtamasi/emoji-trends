package com.acme.emojitrends.stream

import akka.event.LoggingAdapter
import akka.stream._
import akka.stream.scaladsl._
import com.acme.emojitrends.stream.EmojiCounter.Configuration
import twitter4j.{Status, StatusAdapter}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

class EmojiCounter
(configuration: Configuration)
  (implicit val materializer: Materializer,
    executionContext: ExecutionContext,
    loggingAdapter: LoggingAdapter
  ) extends StatusAdapter {

  private val overflowStrategy = OverflowStrategy.backpressure
  private val bufferSize = 1000
  private val statusSource = Source.queue[Status](
    bufferSize,
    overflowStrategy
  )

  private val graph = statusSource
    .log("QUEUED", { status => status.getText })
    .to(Sink.ignore)

  private val queue = graph.run()

  override def onStatus(status: Status): Unit =
    Await.result(queue.offer(status), Duration.Inf)
}

object EmojiCounter {
  case class Configuration(bufferSize: Int)
}
