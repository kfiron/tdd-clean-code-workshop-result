package com.workshop

import com.workshop.framework.FakeClock
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

import scala.concurrent.duration._

class RollingWindowThrottlerTest extends SpecificationWithJUnit {

  class ThrottlerScope extends Scope {
    val clock = new FakeClock()
    val anIp = "192.168.2.1"
    val aThrottler = new RollingWindowThrottler(
      max = 1,
      durationWindow = 1.minute,
      clock = clock)
  }

  "RollingWindowThrottler" should {
    "Allow single request" in new ThrottlerScope {
      aThrottler.tryAcquire(anIp) must beSuccessfulTry
    }
    "Throttle second request which exceeded the max" in new ThrottlerScope {
      aThrottler.tryAcquire(anIp) must beSuccessfulTry
      aThrottler.tryAcquire(anIp) must beFailedTry.withThrowable[ThrottlerException]
    }
    "Allow second request but with different key" in new ThrottlerScope {
      val anotherIp = "200.200.200.1"
      aThrottler.tryAcquire(anIp) must beSuccessfulTry
      aThrottler.tryAcquire(anotherIp) must beSuccessfulTry
    }
    "Re-Allow reuqest after the rolling window" in new ThrottlerScope {
      aThrottler.tryAcquire(anIp) must beSuccessfulTry
      aThrottler.tryAcquire(anIp) must beFailedTry
      clock.age(1.minute)
      aThrottler.tryAcquire(anIp) must beSuccessfulTry
    }
  }

}







