package com.workshop

import java.util.concurrent.atomic.AtomicLong

import scala.concurrent.duration.FiniteDuration


class RollingWindowThrottler(durationWindow: FiniteDuration, max: Int) {


  private val invocationInfo = collection.mutable.HashMap.empty[String, AtomicLong]

  def tryAcquire(key: String): Boolean = {
    invocationInfo.getOrElseUpdate(key, new AtomicLong(0)).incrementAndGet() <= max
  }
}
