package com.workshop

import java.time.{Instant, Clock}

import com.workshop.framework.FakeClock
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope
import scala.concurrent.duration._

import scala.util.{Failure, Success, Try}

class RollingWindowThrottlerTest extends SpecificationWithJUnit {

  class ThrottlerScope extends Scope {
    val clock = new FakeClock
    val anIp = "192.168.2.1"
    val aThrottler = new RollingWindowThrottler(
      max = 1,
      durationWindow = 1.minute,
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
      clock.age(1.minute)
      aThrottler.tryAcquire(anIp) must beSuccessfulTry
    }
  }

}

class RollingWindowThrottler(
                              max: Int = 1,
                              durationWindow: FiniteDuration,
                              clock: Clock) {

  val invocations = scala.collection.mutable.HashMap.empty[String, Invocation]

  def tryAcquire(key: String): Try[Unit] = {
    val invocation = invocations.getOrElseUpdate(key, Invocation(clock.instant(), Counter()))
    if((clock.instant().toEpochMilli - invocation.timestamp.toEpochMilli) >= durationWindow.toMillis){
      invocations -= key
      tryAcquire(key)
    } else if (invocation.counter.incrementAndGet <= max) {
      Success()
    } else {
      Failure(new ThrottleException)
    }

  }
}


case class Counter(var count: Int = 0) {
  def incrementAndGet: Int = {
    count += 1
    count
  }
}

case class Invocation(timestamp: Instant, counter: Counter)
class ThrottleException extends Throwable