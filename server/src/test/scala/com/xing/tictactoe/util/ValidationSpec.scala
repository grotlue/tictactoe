package com.xing.tictactoe.util

class ValidationSpec extends org.specs2.mutable.Specification {

  "Validation" should {
    "Username is non-empty" in {
      Validation.checkNonEmpty("username", "123").toEither must beRight("123")
    }
  }
}
