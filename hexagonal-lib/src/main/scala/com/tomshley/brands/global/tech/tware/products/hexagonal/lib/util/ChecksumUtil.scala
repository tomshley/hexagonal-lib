package com.tomshley.brands.global.tech.tware.products.hexagonal.lib.util

import java.security.MessageDigest

trait ChecksumUtil {
  def toMD5(contents: String): String = {
    MessageDigest.getInstance("MD5").digest(contents.getBytes()).map(0xFF & _).map {
      "%02x".format(_)
    }.foldLeft("") {
      _ + _
    }
  }
}
