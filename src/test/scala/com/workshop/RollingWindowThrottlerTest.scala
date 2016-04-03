package com.workshop

import org.specs2.mutable.SpecificationWithJUnit

import scala.util.{Failure, Success, Try}

class RollingWindowThrottlerTest extends SpecificationWithJUnit{

  "RollingWindowThrottler" should {
    "allow single request" in {
      val throttler = new RollingWindowThrottler()
      throttler.tryAcquire("192.168.2.1") must beSuccessfulTry
    }
    "throttle second request which exceeded the max" in {
      val throttler = new RollingWindowThrottler(max = 1)
      throttler.tryAcquire("192.168.2.1") must beSuccessfulTry
      throttler.tryAcquire("192.168.2.1") must beFailedTry
    }
  }

}

class RollingWindowThrottler(max: Int = 1) {

  val counter = Counter()

  def tryAcquire(key: String): Try[Unit] = {
    if(counter.incrementAndGet <= max){
      Success()
    }else{
      Failure(new Exception)
    }

  }
}


case class Counter(var count: Int = 0){
  def incrementAndGet: Int = {
    count += 1
    count
  }
}