package com.workshop.framework

import scala.concurrent.duration.FiniteDuration


// This will be part of the template, i will not code this live
trait Clock {
  def currentMillis : Long
}

class FakeClock(now: Long) extends Clock{
  private var millis = now
  override def currentMillis: Long = millis

  def age(duration: FiniteDuration): Unit ={
    millis = millis + duration.toMillis
  }
}
