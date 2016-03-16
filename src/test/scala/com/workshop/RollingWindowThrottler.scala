package com.workshop

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

import com.google.common.base.Ticker
import com.google.common.cache.{CacheLoader, CacheBuilder, LoadingCache}
import com.workshop.framework.ClockTicker

import scala.concurrent.duration.FiniteDuration


class RollingWindowThrottler(durationWindow: FiniteDuration, max: Int, clockTicker: ClockTicker) {

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
      clockTicker.now
    }
  }


  def defaultLoadingCache(): CacheLoader[String, AtomicLong] =
    new CacheLoader[String, AtomicLong] {
      override def load(k: String): AtomicLong = {
        new AtomicLong(0)
      }
    }



}


