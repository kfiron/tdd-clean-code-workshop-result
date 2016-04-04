package com.workshop

import java.time.Duration

import com.workshop.framework.FakeClock
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope

class RollingWindowThrottlerTest extends SpecWithJUnit {

  abstract class ThrottlerScope extends Scope {
    val clock = new FakeClock
    val anIp = "192.168.2.1"
    val aThrottler = new RollingWindowThrottler(
      max = 1,
      durationWindow = Duration.ofMinutes(1),
      clock = clock)
  }

  "RollingWindowThrottler" should {
    "allow single request" in new ThrottlerScope {
      aThrottler.tryAcquire(anIp) must beSuccessfulTry
    }
    "throttle second request which exceeded the max" in new ThrottlerScope {
      aThrottler.tryAcquire(anIp) must beSuccessfulTry
      aThrottler.tryAcquire(anIp) must beFailedTry.withThrowable[ThrottleException]
    }
    "allow second request but with different key" in new ThrottlerScope {
      val anotherIp = "200.200.200.1"
      aThrottler.tryAcquire(anIp) must beSuccessfulTry
      aThrottler.tryAcquire(anotherIp) must beSuccessfulTry
    }
    "Re-Allow request after rolling window" in new ThrottlerScope {
      aThrottler.tryAcquire(anIp) must beSuccessfulTry
      aThrottler.tryAcquire(anIp) must beFailedTry.withThrowable[ThrottleException]
      clock.age(Duration.ofMinutes(1))
      aThrottler.tryAcquire(anIp) must beSuccessfulTry
    }
  }

}
