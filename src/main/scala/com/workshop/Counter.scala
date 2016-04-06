package com.workshop

/**
 * Created by kfirb on 4/6/16.
 */
case class Counter(var count: Int = 0) {
  def incrementAndGet: Int = {
    count += 1
    count
  }
}
