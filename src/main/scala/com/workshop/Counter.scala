package com.workshop

case class Counter(var count: Int = 0) {
  def incrementAndGet: Int = {
    count += 1
    count
  }
}
