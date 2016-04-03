package com.workshop

import org.specs2.mutable.SpecificationWithJUnit

import scala.util.{Success, Try}

class RollingWindowThrottlerTest extends SpecificationWithJUnit{

  "RollingWindowThrottler" should {
    "allow single request" in {
      val throttler = new RollingWindowThrottler()
      throttler.tryAcquire("192.168.2.1") must beSuccessfulTry
    }
  }

}

class RollingWindowThrottler() {
  def tryAcquire(key: String): Try[Unit] = {
    Success(Unit)
  }
}
