package com.acme.emojitrends.persistence

import javax.inject.Inject

import com.redis.RedisClientPool

class RedisEmojiCounterDao @Inject()(pool: RedisClientPool) {
  def incrementTweets: Option[Long] =
    pool.withClient { r =>
      r.incr("tweets")
    }

  def incrementEmoji(emoji: String): Option[Long] =
    pool.withClient { r =>
      r.incr(emojiKey(emoji))
    }

  def idf(emoji: String): Double =
    math.log1p(tweets.toDouble / occurrences(emoji))

  def tweets: Int =
    pool.withClient { r =>
      r.get("tweets") map {_.toInt} getOrElse 0
    }

  def occurrences(emoji: String): Int =
    pool.withClient { r =>
      r.get(emojiKey(emoji)) map {_.toInt} getOrElse 0
    }

  private def emojiKey(emoji: String) =
    s"emoji:$emoji"
}
