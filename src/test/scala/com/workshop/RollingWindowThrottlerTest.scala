package com.workshop

import com.workshop.framework.FakeClock
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope

import scala.concurrent.duration._

class RollingWindowThrottlerTest extends SpecWithJUnit {

  class ThrottlerScope extends Scope{
    val clock = new FakeClock()
    val anIp = "192.168.2.1"
    val aThrottler = new RollingWindowThrottler(
      max = 1,
      durationWindow = 1.minutes,
      clock = clock)
  }

  "RollingWindowThrottler" should {
    "Allow request" in new ThrottlerScope{
      aThrottler.tryAcquire(anIp) must beSuccessfulTry
    }
    "Throttle request which exceeded the limit" in new ThrottlerScope{
      aThrottler.tryAcquire(anIp) must beSuccessfulTry
      aThrottler.tryAcquire(anIp) must beFailedTry.withThrowable[ThrottleException]
    }
    "Allow two different requests with different keys" in new ThrottlerScope{
      val anotherIp = "200.200.200.1"
      aThrottler.tryAcquire(anIp) must beSuccessfulTry
      aThrottler.tryAcquire(anotherIp) must beSuccessfulTry
    }
    "Re-Allow requests after rolling window ended" in new ThrottlerScope{
      aThrottler.tryAcquire(anIp) must beSuccessfulTry
      clock.age(1.minute)
      aThrottler.tryAcquire(anIp) must beSuccessfulTry
    }
  }

}