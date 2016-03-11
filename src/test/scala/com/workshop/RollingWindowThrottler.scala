package com.workshop

import java.time.Clock
import java.util.concurrent.atomic.AtomicLong

import scala.concurrent.duration.FiniteDuration


class RollingWindowThrottler(durationWindow: FiniteDuration, max: Int, clock: Clock) {


  private val invocationInfo = collection.mutable.HashMap.empty[String, InvocationInfo]

  def tryAcquire(key: String): Boolean = {
    val invocation = invocationInfo.getOrElseUpdate(key, newInvocationInfo)
    acquire((key, invocation))
  }

  def acquire = reset orElse validate

  private def reset: PartialFunction[(String, InvocationInfo), Boolean] = {
    case (key, invocation) if expires(invocation) => {
      invocationInfo.-=(key)
      tryAcquire(key)
    }
  }

  private def validate: PartialFunction[(String, InvocationInfo), Boolean] = {
    case(key, invocation) => invocation.counter.incrementAndGet() <= max
  }

  private def newInvocationInfo = new InvocationInfo(counter = new AtomicLong(0),
    clock.millis())

  private def expires(invocation: InvocationInfo) = (clock.millis() - invocation.timeStamp) > durationWindow.toMillis
}

case class InvocationInfo(counter: AtomicLong, timeStamp: Long)
