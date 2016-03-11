package com.workshop


import com.workshop.framework.FakeClock
import org.specs2.matcher.{Matchers, Scope}
import org.specs2.mutable.SpecWithJUnit
import scala.concurrent.duration._


class RollingWindowThrottlerTest extends SpecWithJUnit with Matchers{

  trait Context extends Scope{
    val clock = new FakeClock
    val aThrottler = new RollingWindowThrottler(durationWindow = 1.minute, max = 1, clock = clock)
    val anIp = "192.168.2.1"
  }

  "RollingWindowThrottler" should {
    "allow request within the sliding window" in new Context  {
      aThrottler.tryAcquire(anIp) must beAllowedRequest(anIp)
    }
    "throttle request which exceed the sliding window" in new Context {
      aThrottler.tryAcquire(anIp) must beAllowedRequest(anIp)
      aThrottler.tryAcquire(anIp) must beThrottledRequest(anIp)
    }
    "allow 2 requests with different key" in new Context {
      val anotherIp = "192.168.10.10"
      aThrottler.tryAcquire(anIp) must beAllowedRequest(anIp)
      aThrottler.tryAcquire(anotherIp) must beAllowedRequest(anIp)
    }
    "re-allow request after ending of the sliding window" in new Context {
      aThrottler.tryAcquire(anIp) must beAllowedRequest(anIp)
      aThrottler.tryAcquire(anIp) must beThrottledRequest(anIp)
      clock.age(2.minutes)
      aThrottler.tryAcquire(anIp) must beAllowedRequest(anIp)
    }
  }

  def beAllowedRequest(key: String) = beTrue ^^
    { (b: Boolean) => b aka s"expected invocation key $key to be allowed but" }

  def beThrottledRequest(key: String) = beFalse ^^
    { (b: Boolean) => b aka s"expected invocation key $key to be throttled but" }

}
