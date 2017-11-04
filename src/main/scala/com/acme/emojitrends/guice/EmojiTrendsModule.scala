package com.acme.emojitrends.guice

import javax.inject.Singleton

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.stream.Materializer
import com.acme.emojitrends.stream.EmojiCounter
import com.google.inject.{AbstractModule, Provides}
import com.typesafe.config.ConfigFactory
import net.codingwell.scalaguice.ScalaModule

import scala.concurrent.ExecutionContext

class EmojiTrendsModule extends AbstractModule with ScalaModule {
  private lazy val config = ConfigFactory.load

  override def configure(): Unit = {
    install(new TwitterModule(config.getConfig("twitter")))
    install(new AkkaModule(config.getConfig("akka")))
  }

  @Provides
  @Singleton
  def provideEmojiCounter
    (implicit system: ActorSystem,
      ec: ExecutionContext,
      materializer: Materializer): EmojiCounter = {

    implicit val adapter: LoggingAdapter = Logging(system, classOf[EmojiCounter])
    val counterConfig = config.getConfig("emojiCounter")
    val configuration = EmojiCounter.Configuration(
      counterConfig.getInt("bufferSize")
    )
    new EmojiCounter(configuration)
  }
}
