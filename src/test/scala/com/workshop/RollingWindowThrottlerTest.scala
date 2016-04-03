package com.workshop

import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

import scala.util.{Failure, Success, Try}

class RollingWindowThrottlerTest extends SpecificationWithJUnit{

  class ThrottlerScope extends Scope{
    val anIp = "192.168.2.1"
    val aThrottler = new RollingWindowThrottler(max = 1)
  }

  "RollingWindowThrottler" should {
    "allow single request" in new ThrottlerScope{
      aThrottler.tryAcquire(anIp) must beSuccessfulTry
    }
    "throttle second request which exceeded the max" in new ThrottlerScope{
      aThrottler.tryAcquire(anIp) must beSuccessfulTry
      aThrottler.tryAcquire(anIp) must beFailedTry
    }
    "allow second request but with different key" in new ThrottlerScope{
      val anotherIp = "200.200.200.1"
      aThrottler.tryAcquire(anIp) must beSuccessfulTry
      aThrottler.tryAcquire(anotherIp) must beSuccessfulTry
    }
  }

}

class RollingWindowThrottler(max: Int = 1) {

  val invocations = scala.collection.mutable.HashMap.empty[String, Counter]

  def tryAcquire(key: String): Try[Unit] = {
    val counter = invocations.getOrElseUpdate(key, Counter())
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