package com.workshop

import scala.concurrent.duration.FiniteDuration


class RollingWindowThrottler(durationWindow: FiniteDuration, max: Int) {
  def tryAcquire(key: String): Boolean = true
}
