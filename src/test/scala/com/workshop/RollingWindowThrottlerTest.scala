package com.workshop


import org.specs2.mutable.SpecWithJUnit
import scala.concurrent.duration._


class RollingWindowThrottlerTest extends SpecWithJUnit{

  "RollingWindowThrottler" should {
    "allow request within the sliding window" in {
      val throttler = new RollingWindowThrottler(durationWindow = 1.minute, max = 1)
      throttler.tryAcquire("192.168.2.1") must beTrue
    }
  }

}
