package com.workshop

import org.specs2.mutable.SpecificationWithJUnit

import scala.util.{Failure, Success, Try}

class RollingWindowThrottlerTest extends SpecificationWithJUnit{

  "RollingWindowThrottler" should {
    "Allow one request" in {
      val throttler = new RollingWindowThrottler()
      throttler.tryAcquire("192.168.2.1") must beSuccessfulTry
    }
    "Does not allow more then max requests" in {
      val throttler = new RollingWindowThrottler(max = 1)
      throttler.tryAcquire("192.168.2.1") must beSuccessfulTry
      throttler.tryAcquire("192.168.2.1") must beFailedTry
    }
  }

}

class RollingWindowThrottler(max: Int = 1) {

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
