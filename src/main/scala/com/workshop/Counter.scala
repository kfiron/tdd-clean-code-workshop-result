package com.workshop

/**
 * Created by kfirb on 4/3/16.
 */
case class Counter(var count: Int = 0) {
  def inc {
    count += 1
  }
}
