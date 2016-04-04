
package com.workshop.framework

import java.time._
import java.time.temporal.TemporalAmount

class FakeClock(private var currentClockTime: Instant = Clock.systemUTC().instant()) extends Clock {
  override def getZone: ZoneId = ZoneOffset.UTC

  override def instant(): Instant = currentClockTime

  override def withZone(zone: ZoneId): Clock = Clock.fixed(currentClockTime, zone)

  def age(temporalAmount: TemporalAmount) = {
    currentClockTime = currentClockTime.plus(temporalAmount)
  }
}
