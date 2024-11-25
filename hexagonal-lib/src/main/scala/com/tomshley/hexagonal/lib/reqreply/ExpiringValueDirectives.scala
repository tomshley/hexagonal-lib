package com.tomshley.hexagonal.lib.reqreply

import com.tomshley.hexagonal.lib.reqreply.exceptions.{ExpiredExpiringValueRejection, ExpiringValueRejection}
import com.tomshley.hexagonal.lib.reqreply.models.ExpiringValue
import org.apache.pekko.http.scaladsl.server.Directive1
import org.apache.pekko.http.scaladsl.server.Directives.*

import scala.util.matching.Regex

trait ExpiringValueDirectives {

  final def pathMatcher: Regex = ExpiringValue.base64URLSafeRegex

  def expiringValue(matchedPath:String):Directive1[ExpiringValue] = {
    ExpiringValue.fromBase64Hmac(matchedPath) match
      case Some(expiringValue) =>
        if expiringValue.isExpired then reject(
          ExpiredExpiringValueRejection("The value has expired")
        ) else {
          provide(expiringValue)
        }
      case None => reject(ExpiringValueRejection("Unable to Validate the Expiring Value"))
  }
}

object ExpiringValueDirectives extends ExpiringValueDirectives


