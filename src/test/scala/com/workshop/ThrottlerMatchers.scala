package com.workshop

import org.specs2.matcher.Matchers


trait ThrottlerMatchers extends Matchers{
  def beThrottledRequest(key: String) = beFalse ^^
    { (b: Boolean) => b aka s"expected invocation by key $key to be throttled but it was" }
  def beAllowedRequest(key: String) = beTrue ^^
    { (b: Boolean) => b aka s"expected invocation by key  $key to be allowed byt it was " }
}
