package com.workshop

import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope

import scala.util.Try

class RollingWindowThrottlerTest extends SpecWithJUnit {

  class ThrottlerScope extends Scope{
    val anIp = "192.168.2.1"
    val aThrottler = new RollingWindowThrottler(max = 1)
  }

  "RollingWindowThrottler" should {
    "Allow request" in new ThrottlerScope{
      aThrottler.tryAcquire(anIp) must beSuccessfulTry
    }
    "Throttle request which exceeded the limit" in new ThrottlerScope{
      aThrottler.tryAcquire(anIp) must beSuccessfulTry
      aThrottler.tryAcquire(anIp) must beFailedTry
    }
  }

}

class RollingWindowThrottler(max: Int = 1) {
  val counter = Counter()
  def tryAcquire(key: String): Try[Unit] = {
    Try{
      counter.inc
      if(counter.count > max){
        throw new Exception
      }
    }
  }
}

case class Counter(var count: Int = 0) {
  def inc {
    count += 1
  }
}
