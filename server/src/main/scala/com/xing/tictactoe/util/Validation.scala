package com.xing.tictactoe.util

import cats.data.Validated
import cats.syntax.validated._

object Validation {

  type ErrorsOr[A] = Validated[List[String], A]

  def checkNonEmpty(fieldName: String, text: String): ErrorsOr[String] = {
    if (text.nonEmpty) {
      text.valid[List[String]]
    } else {
      List(s"Given $fieldName should not be empty").invalid[String]
    }
  }
}
