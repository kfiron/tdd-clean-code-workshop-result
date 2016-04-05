package com.workshop

import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

import scala.util.{Failure, Success, Try}

class RollingWindowThrottlerTest extends SpecificationWithJUnit{

  class RollingWindowScope extends Scope{
    val anIp = "192.168.2.1"
    val aThrottler = new RollingWindowThrottler(max = 1)
  }

  "RollingWindowThrottler" should {
    "Allow one request" in new RollingWindowScope{
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
    }
  }

}

class RollingWindowThrottler(max: Int) {

  val counter = Counter()
  val invocations = scala.collection.mutable.HashMap.empty[String, Counter]

  def tryAcquire(key: String): Try[Unit] = {
    val count = invocations.getOrElseUpdate(key, Counter()).incrementAndGet
    if(count <= max) {
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
