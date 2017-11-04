package com.acme.emojitrends

import com.acme.emojitrends.guice.EmojiTrendsModule
import com.acme.emojitrends.stream.EmojiCounter
import com.google.inject.Guice
import com.typesafe.scalalogging.LazyLogging
import net.codingwell.scalaguice.InjectorExtensions.ScalaInjector
import twitter4j.TwitterStream

object EmojiTrends extends App with LazyLogging {
  private val emojiTrendsModule = new EmojiTrendsModule
  val injector = new ScalaInjector(Guice.createInjector(emojiTrendsModule))

  val twitterStream = injector.instance[TwitterStream]
  val counter = injector.instance[EmojiCounter]
  twitterStream.addListener(counter)

  twitterStream.sample()
}
