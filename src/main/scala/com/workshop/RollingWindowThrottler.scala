package com.workshop

import java.time.Clock
import java.util.concurrent.TimeUnit

import com.google.common.base.Ticker
import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}

import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success, Try}

/**
 * Created by kfirb on 4/4/16.
 */
class RollingWindowThrottler(
                              max: Int = 1,
                              durationWindow: FiniteDuration,
                              clock: Clock) {

  val invocations : LoadingCache[String, Counter] = CacheBuilder.newBuilder()
        .expireAfterWrite(durationWindow.toMillis, TimeUnit.MILLISECONDS)
        .ticker(throttlerTicker())
        .build(defaultCounter())

  def tryAcquire(key: String): Try[Unit] = {
    if(invocations.get(key).incrementAndGet <= max){
      Success()
    }else {
      Failure(new ThrottleException)
    }
  }

  def defaultCounter(): CacheLoader[String, Counter] = new CacheLoader[String, Counter] {
    override def load(key: String): Counter = Counter()
  }

  def throttlerTicker(): Ticker = new Ticker {
    override def read(): Long = TimeUnit.MILLISECONDS.toNanos(clock.instant().toEpochMilli)
  }
}
