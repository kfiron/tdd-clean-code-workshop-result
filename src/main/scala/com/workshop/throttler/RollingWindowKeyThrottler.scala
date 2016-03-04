package com.workshop.throttler

import java.util.concurrent.atomic.AtomicInteger
import com.workshop.framework.Clock
import scala.concurrent.duration.FiniteDuration


class RollingWindowKeyThrottler(durationWindow: FiniteDuration, max: Int, clock: Clock) {

  val map = collection.mutable.HashMap.empty[String, InvocationInfo]

  final def tryAcquire(key: String): Boolean = {
    val item = map.getOrElseUpdate(key, defaultInvocationInfo)
    _tryAcquire((key, item))
  }

  private def _tryAcquire = slidingWindowEnded orElse incrementAndGet

  private def slidingWindowEnded: PartialFunction[(String, InvocationInfo), Boolean] = {
    case (key,item) if (clock.currentMillis - item.timestamp) > durationWindow.toMillis =>
      map -= key
      tryAcquire(key)
  }


  private def incrementAndGet: PartialFunction[(String, InvocationInfo), Boolean] = {
    case (key,item) => item.count.incrementAndGet() <= max
  }

  private def defaultInvocationInfo = {
    InvocationInfo(clock.currentMillis, new AtomicInteger(0))
  }
}

case class InvocationInfo(timestamp: Long, count: AtomicInteger)
