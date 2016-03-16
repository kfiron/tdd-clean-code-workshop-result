package com.workshop.framework

import java.time.Clock
import java.util.concurrent.TimeUnit
import scala.concurrent.duration._


trait ClockTicker {
  def now : Long
}

class FakeClockTicker extends ClockTicker{

  private var currentClock = Clock.systemUTC()

  override def now: Long =  TimeUnit.MILLISECONDS.toNanos(currentClock.millis())
  def age(duration: FiniteDuration) {
    currentClock = Clock.offset(currentClock, java.time.Duration.ofMillis(duration.toMillis))
  }
}

class DefaultClockTicker extends ClockTicker{
  override def now: Long = TimeUnit.MILLISECONDS.toNanos(Clock.systemUTC().millis())
}
