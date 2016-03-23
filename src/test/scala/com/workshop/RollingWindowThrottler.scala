package com.workshop

import java.time.Clock
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

import com.google.common.base.Ticker
import com.google.common.cache.{CacheLoader, CacheBuilder, LoadingCache}

import scala.concurrent.duration.FiniteDuration


class RollingWindowThrottler(durationWindow: FiniteDuration, max: Int, clock: Clock) {

  private val invocations : LoadingCache[String, AtomicLong] =
    CacheBuilder.newBuilder()
      .expireAfterWrite(durationWindow.toMillis, TimeUnit.MILLISECONDS)
      .ticker(throttlerTicker())
      .build(defaultLoadingCache())




  def tryAcquire(key: String): Boolean = {
    invocations.get(key).incrementAndGet() <= max
  }



  def throttlerTicker(): Ticker = new Ticker {
    override def read(): Long = {
      TimeUnit.MILLISECONDS.toNanos(clock.instant().toEpochMilli)
    }
  }


  def defaultLoadingCache(): CacheLoader[String, AtomicLong] =
    new CacheLoader[String, AtomicLong] {
      override def load(k: String): AtomicLong = {
        new AtomicLong(0)
      }
    }



}


