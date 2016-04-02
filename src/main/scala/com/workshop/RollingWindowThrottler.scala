package com.workshop

import java.time.Clock

import com.google.common.cache.{CacheLoader, CacheBuilder, LoadingCache}

import scala.concurrent.duration.FiniteDuration
import scala.util.Try

class RollingWindowThrottler(
                              max: Int = 1,
                              durationWindow: FiniteDuration,
                              clock: Clock) {


  def defaultInvocation(): CacheLoader[String, Counter] = new CacheLoader[String, Counter] {
    override def load(k: String): Counter = Counter()
  }

  val invocations : LoadingCache[String, Counter] = CacheBuilder.newBuilder()
     .build(defaultInvocation())

  def tryAcquire(key: String): Try[Unit] = {
    val counter = invocations.get(key)
    counter.inc
    Try{
      if(counter.count > max){
        throw new ThrottleException
      }
    }
  }

}
