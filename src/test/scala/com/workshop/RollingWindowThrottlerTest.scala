package com.workshop

import org.specs2.mutable.SpecWithJUnit

import scala.util.Try

class RollingWindowThrottlerTest extends SpecWithJUnit{

  "RollingWindowThrottler" should {
    "Allow request" in {
      val throttler = new RollingWindowThrottler()
      throttler.tryAcquire("192.168.2.1") must beSuccessfulTry
    }
    "Throttle request which exceeded the limit" in {
      val throttler = new RollingWindowThrottler(max = 1)
      throttler.tryAcquire("192.168.2.1") must beSuccessfulTry
      throttler.tryAcquire("192.168.2.1") must beFailedTry
    }
  }

}

class RollingWindowThrottler(max: Int = 1) {
  def tryAcquire(key: String): Try[Unit] = Try()
}
