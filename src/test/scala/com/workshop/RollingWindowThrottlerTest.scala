package com.workshop


import com.workshop.framework.FakeClock
import com.workshop.throttler.RollingWindowKeyThrottler
import org.joda.time.DateTime
import org.specs2.matcher.{Matchers, Scope}
import org.specs2.mutable.SpecificationWithJUnit
import scala.concurrent.duration._


class RollingWindowThrottlerTest
  extends SpecificationWithJUnit
  with ThrottlerMatchers{

  trait Context extends Scope{
    val clock = new FakeClock(new DateTime().getMillis)
    val anIp = "192.168.2.1"
    val anotherIp = "192.168.2.2"
    val aThrottler = new RollingWindowKeyThrottler(durationWindow = 1.minute,
      max = 1,
      clock = clock)
  }

  "RollingWindowThrottler" should {
    "allow request in the sliding window" in new Context{
      val allowed = aThrottler.tryAcquire(anIp)
      allowed must beAllowedRequest(anIp)
    }
    "allow 2 different requests in the sliding window" in new Context{
      aThrottler.tryAcquire(anIp)  must beAllowedRequest(anIp)
      aThrottler.tryAcquire(anotherIp)  must beAllowedRequest(anIp)

    }
    "not allow request above the max in the sliding window" in new Context{
      aThrottler.tryAcquire(anIp) must beAllowedRequest(anIp)
      aThrottler.tryAcquire(anIp) must beThrottledRequest(anIp)
    }
    "allow second request for the same IP after the sliding window is ended" in new Context{
      aThrottler.tryAcquire(anIp) must beAllowedRequest(anIp)
      clock.age(2.minute)
      aThrottler.tryAcquire(anIp) must beAllowedRequest(anIp)
    }
    "not allow 3rd request for the same ip after second sliding window" in new Context{
      aThrottler.tryAcquire(anIp) must beAllowedRequest(anIp)
      clock.age(2.minute)
      aThrottler.tryAcquire(anIp) must beAllowedRequest(anIp)
      aThrottler.tryAcquire(anIp) must beThrottledRequest(anIp)
    }
  }



}

