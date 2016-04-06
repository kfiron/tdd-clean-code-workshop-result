package com.workshop

import java.time.Clock
import java.util.concurrent.TimeUnit

import com.google.common.base.Ticker
import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}

import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success, Try}

/**
 * Created by kfirb on 4/6/16.
 */
class RollingWindowThrottler(max: Int = 1,
                             durationWindow: FiniteDuration,
                             clock: Clock) {


  val invocations: LoadingCache[String, Counter] = CacheBuilder.newBuilder()
    .expireAfterWrite(durationWindow.toMillis, TimeUnit.MILLISECONDS)
    .ticker(throttlingTicker())
    .build(defaultCacheObject())


  def tryAcquire(key: String): Try[Unit] = {
    invocations.get(key).incrementAndGet <= max
  }


  def defaultCacheObject(): CacheLoader[String, Counter] = new CacheLoader[String, Counter] {
    override def load(k: String): Counter = Counter()
  }

  def throttlingTicker(): Ticker = new Ticker {
    override def read(): Long = TimeUnit.MILLISECONDS.toNanos(clock.instant().toEpochMilli)
  }

  implicit def booleanToTry(b: Boolean): Try[Unit] = {
    if (b) {
      Success()
    } else {
      Failure(new ThrottlerException)
    }
  }
}
