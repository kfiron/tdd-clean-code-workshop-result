package com.workshop.framework

import java.time.LocalDateTime
import scala.concurrent.duration._


object ClockApp extends App{

  val myClock = new FakeClock()
  println(LocalDateTime.now(myClock))
  myClock.age(1.hour)
  println(LocalDateTime.now(myClock))

}
