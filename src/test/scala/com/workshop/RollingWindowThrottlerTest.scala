package com.workshop


import org.specs2.matcher.Scope
import org.specs2.mutable.SpecWithJUnit
import scala.concurrent.duration._


class RollingWindowThrottlerTest extends SpecWithJUnit{

  trait Context extends Scope{
    val aThrottler = new RollingWindowThrottler(durationWindow = 1.minute, max = 1)
    val anIp = "192.168.2.1"
  }

  "RollingWindowThrottler" should {
    "allow request within the sliding window" in new Context  {
      aThrottler.tryAcquire(anIp) must beTrue
    }
    "throttle request which exceed the sliding window" in new Context {
      aThrottler.tryAcquire(anIp) must beTrue
      aThrottler.tryAcquire(anIp) must beFalse
    }
    "allow 2 requests with different key" in new Context {
      val anotherIp = "192.168.10.10"
      aThrottler.tryAcquire(anIp) must beTrue
      aThrottler.tryAcquire(anotherIp) must beTrue
    }
  }

}
