package com.tomshley.hexagonal.lib.reqreply.models

import com.tomshley.hexagonal.lib.utils.InsecureSaltedEncryptionUtil

import java.time.Instant
import java.util.UUID
import scala.concurrent.duration.*

private object RequestIdSettings {
  final val splitSeparator = ";"
}

final case class ExpiringValue(uuid: UUID, expiration: Instant, value:Option[String])
    extends InsecureSaltedEncryptionUtil {
  private def toSplittableString = {
    val baseString = Seq(uuid.toString, expiration.toString)
    (value match
      case Some(value) => baseString :+ value
      case None => baseString).mkString(RequestIdSettings.splitSeparator)
  }

  def toBase64Hmac: String = {
    encryptBase64Hmac(toSplittableString)
  }

  def isValid: Boolean = {
    !isExpired
  }

  def isExpired: Boolean = {
    Instant.now().isAfter(expiration)
  }
}

object ExpiringValue extends InsecureSaltedEncryptionUtil {
  def apply(expirationDuration: Option[Duration] = None, value:Option[String] = None) =
    new ExpiringValue(
      UUID.randomUUID(),
      Instant.now().plusMillis(expirationDuration.getOrElse(5.minutes).toMillis),
      value
    )

  def fromBase64Hmac(hashString: String): Option[ExpiringValue] = {
    try {
      val splitHash = decryptBase64Hmac(hashString)
        .split(RequestIdSettings.splitSeparator)
      Some(
        ExpiringValue(
          UUID.fromString(splitHash(0)),
          Instant.parse(splitHash(1)),
          if splitHash.length > 2 then Some(splitHash(2)) else None
        )
      )
    } catch {
      case _: Any => None
    }
  }

  final class ExpiringValueInvalid(message: String)
      extends Exception(message)
}


