package com.tomshley.hexagonal.lib.http2.extraction.formfield

import scala.util.matching.Regex

trait Validations {
  private final val emailExpression: Regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".r

  def isValidEmailFormat(email: String): Boolean = {

    emailExpression.findFirstMatchIn(email) match {
      case Some(_) => true
      case None => false
    }
  }

  def isValidPhoneFormat(email: String): Boolean = {
    emailExpression.findFirstMatchIn(email) match {
      case Some(_) => true
      case None => false
    }
  }

  def isShortEnough(message: String, limit: Int): Boolean = {
    message.length <= limit
  }
}
