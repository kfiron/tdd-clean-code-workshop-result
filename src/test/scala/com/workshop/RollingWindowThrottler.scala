package com.workshop

import java.time.Clock
import java.util.concurrent.atomic.AtomicLong

import scala.concurrent.duration.FiniteDuration


class RollingWindowThrottler(durationWindow: FiniteDuration, max: Int, clock: Clock) {


  private val invocationInfo = collection.mutable.HashMap.empty[String, InvocationInfo]

  def tryAcquire(key: String): Boolean = {
    val invocation = invocationInfo.getOrElseUpdate(key, new InvocationInfo(new AtomicLong(0), clock.millis))
    if ((clock.millis() - invocation.timeStamp) > durationWindow.toMillis){
      invocationInfo.put(key, new InvocationInfo(new AtomicLong(0), clock.millis()))
      true
    }
    else
      invocation.counter.incrementAndGet() <= max

  }
}

case class InvocationInfo(counter: AtomicLong, timeStamp: Long)
