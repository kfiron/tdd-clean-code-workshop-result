package com.workshop

import java.time.{Instant, Clock}
import java.util.concurrent.TimeUnit

import com.google.common.base.Ticker
import com.google.common.cache.{CacheLoader, CacheBuilder, LoadingCache}
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

  val invocations : LoadingCache[String, Counter] = CacheBuilder.newBuilder()
        .expireAfterWrite(durationWindow.toMillis, TimeUnit.MILLISECONDS)
        .ticker(throttlerTicker())
        .build(defaultCounter())

  def tryAcquire(key: String): Try[Unit] = {
    if(invocations.get(key).incrementAndGet <= max){
      Success()
    }else {
      Failure(new ThrottleException)
    }
  }

  def defaultCounter(): CacheLoader[String, Counter] = new CacheLoader[String, Counter] {
    override def load(key: String): Counter = Counter()
  }

  def throttlerTicker(): Ticker = new Ticker {
    override def read(): Long = TimeUnit.MILLISECONDS.toNanos(clock.instant().toEpochMilli)
  }
}


case class Counter(var count: Int = 0) {
  def incrementAndGet: Int = {
    count += 1
    count
  }
}
class ThrottleException extends Throwable