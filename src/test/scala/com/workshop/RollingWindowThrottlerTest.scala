package com.workshop

import java.time.{Instant, Clock}

import com.workshop.framework.FakeClock
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope
import scala.concurrent.duration._

import scala.util.{Failure, Success, Try}

class RollingWindowThrottlerTest extends SpecificationWithJUnit{

  class RollingWindowScope extends Scope{
    val clock = new FakeClock
    val anIp = "192.168.2.1"
    val aThrottler = new RollingWindowThrottler(
      max = 1,
      durationWindow = 1.minute,
      clock = clock)
  }

  "RollingWindowThrottler" should {
   /* "Allow one request" in new RollingWindowScope{
      aThrottler.tryAcquire(anIp) must beSuccessfulTry
    }
    "Throttle request that exceeded max requests" in new RollingWindowScope{
      aThrottler.tryAcquire(anIp) must beSuccessfulTry
      aThrottler.tryAcquire(anIp) must beFailedTry
    }
    "Allow second request but with different key" in new RollingWindowScope{
      val anotherIp = "200.200.200.1"
      aThrottler.tryAcquire(anIp) must beSuccessfulTry
      aThrottler.tryAcquire(anotherIp) must beSuccessfulTry
    }*/
    "Re-allow request after rolling window is ended" in new RollingWindowScope{
      aThrottler.tryAcquire(anIp) must beSuccessfulTry
      aThrottler.tryAcquire(anIp) must beFailedTry
      clock.age(1.minute)
      aThrottler.tryAcquire(anIp) must beSuccessfulTry
    }
  }

}

class RollingWindowThrottler(
                              max: Int,
                              durationWindow: FiniteDuration,
                              clock: Clock) {

  val counter = Counter()
  val invocations = scala.collection.mutable.HashMap.empty[String, Invocation]

  def tryAcquire(key: String): Try[Unit] = {
    val invocation = invocations.getOrElseUpdate(key, Invocation(clock.instant(), Counter()))

    if((clock.instant().toEpochMilli - invocation.timestamp.toEpochMilli) >= durationWindow.toMillis){
      invocations -= key
      tryAcquire(key)
    } else if(invocation.counter.incrementAndGet <= max) {
      Success()
    }else{
      Failure(new Exception)
    }
  }
}

case class Counter(var count: Int = 0){
  def incrementAndGet : Int = {
    count +=1
    count
  }
}

case class Invocation(timestamp: Instant, counter: Counter)
