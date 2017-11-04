package com.acme.emojitrends.guice

import javax.inject.Singleton

import com.google.inject.{AbstractModule, Provides}
import com.redis.RedisClientPool
import com.typesafe.config.Config
import net.codingwell.scalaguice.ScalaModule

class RedisModule(config: Config) extends AbstractModule with ScalaModule {
  override def configure(): Unit = {}

  @Provides
  @Singleton
  def provideRedisClientPool(): RedisClientPool = {
    new RedisClientPool(
      host = config.getString("host"),
      port = config.getInt("port"),
      database = config.getInt("database")
    )
  }
}
