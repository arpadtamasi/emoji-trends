package com.acme.emojitrends.stream

import akka.event.LoggingAdapter
import akka.stream._
import akka.stream.scaladsl._
import com.acme.emojitrends.persistence.RedisEmojiCounterDao
import com.acme.emojitrends.stream.EmojiCounter.Configuration
import com.vdurmont.emoji.EmojiParser
import twitter4j.{Status, StatusAdapter}

import scala.collection.JavaConverters._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

class EmojiCounter
(dao: RedisEmojiCounterDao, configuration: Configuration)
  (implicit val materializer: Materializer,
    executionContext: ExecutionContext,
    loggingAdapter: LoggingAdapter
  ) extends StatusAdapter {

  private val overflowStrategy = OverflowStrategy.backpressure
  private val bufferSize = 1000

  private val statusSource = Source
    .queue[Status](bufferSize, overflowStrategy)

  private val extractEmojis = Flow[Status]
    .map { status =>
      EmojiParser
        .extractEmojis(status.getText).asScala
    }
    .async
    .filter { emojis =>
      emojis.nonEmpty
    }

  private val incrementTweets = Flow[Seq[String]]
    .map { emojis =>
      dao.incrementTweets
      emojis
    }
    .async

  private val incrementEmojis = Flow[Seq[String]]
    .mapConcat { emojis =>
      emojis.distinct.toList
    }
    .async
    .map { emoji =>
      dao.incrementEmoji(emoji)
      emoji
    }
    .log("IDF", {emoji =>
        val idf = math.round(
          math.log1p(dao.tweets.toDouble / dao.occurrences(emoji))
        )

      s"$emoji ${"*" * idf.toInt}"
    })


  private val graph = statusSource
    .via(extractEmojis)
    .via(incrementTweets)
    .via(incrementEmojis)
    .to(Sink.ignore)

  private val queue = graph.run()

  override def onStatus(status: Status): Unit =
    Await.result(queue.offer(status), Duration.Inf)
}

object EmojiCounter {

  case class Configuration(bufferSize: Int)

}
