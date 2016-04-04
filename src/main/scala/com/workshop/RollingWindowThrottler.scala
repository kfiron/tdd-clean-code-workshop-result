package com.workshop

import java.time.{Clock, Duration}
import java.util.concurrent.TimeUnit

import com.google.common.base.Ticker
import com.google.common.cache.{CacheBuilder, CacheLoader}

import scala.language.implicitConversions
import scala.util.Try

class RollingWindowThrottler(max: Int = 1,
                             durationWindow: Duration,
                             clock: Clock) {

  private val invocations = CacheBuilder.newBuilder()
    .expireAfterWrite(durationWindow.toMillis, TimeUnit.MILLISECONDS)
    .ticker(throttlerTicker())
    .build(defaultCounter())

  def tryAcquire(key: String): Try[Unit] = invocations.get(key).incrementAndGet <= max

  private implicit def booleanToTry(b: Boolean): Try[Unit] = Try {
    if (!b) throw new ThrottleException
  }

  private def throttlerTicker(): Ticker = new Ticker {
    override def read(): Long = TimeUnit.MILLISECONDS.toNanos(clock.millis())
  }

  private def defaultCounter(): CacheLoader[String, Counter] = new CacheLoader[String, Counter] {
    override def load(key: String): Counter = Counter()
  }
}
