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
    "Does not allow more then max requests" in new RollingWindowScope{
      aThrottler.tryAcquire("192.168.2.1") must beSuccessfulTry
      aThrottler.tryAcquire("192.168.2.1") must beFailedTry
    }
  }

}

class RollingWindowThrottler(max: Int) {

  val counter = Counter()

  def tryAcquire(key: String): Try[Unit] = {
    val count = counter.incrementAndGet
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
