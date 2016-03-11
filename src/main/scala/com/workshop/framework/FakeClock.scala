package com.workshop.framework

import java.time
import java.time._

import scala.concurrent.duration.FiniteDuration


class FakeClock extends Clock{

  var clock = Clock.systemUTC().toFixed

  override def getZone: ZoneId = clock.getZone
  override def instant(): Instant = clock.instant()
  override def withZone(zone: ZoneId): time.Clock = clock.withZone(zone)

  def age(duration: FiniteDuration) = {
    clock = Clock.offset(clock, Duration.ofMillis(duration.toMillis)).toFixed
  }

  implicit class ToFixed(clock: Clock){
    def toFixed : Clock = Clock.fixed(clock.instant, clock.getZone)
  }

}

