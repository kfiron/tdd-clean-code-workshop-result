
package com.workshop.framework

import java.time.{Clock, Instant, ZoneId, ZoneOffset}

import scala.concurrent.duration.FiniteDuration

class FakeClock(private var currentClockTime: Instant = Clock.systemUTC().instant()) extends Clock {
  override def getZone: ZoneId = ZoneOffset.UTC

  override def instant(): Instant = currentClockTime

  override def withZone(zone: ZoneId): Clock = Clock.fixed(currentClockTime, zone)

  def age(duration: FiniteDuration) = {
    currentClockTime = currentClockTime.plusMillis(duration.toMillis)
  }
}