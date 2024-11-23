package com.tomshley.hexagonal.lib.reqreply.models

import com.tomshley.hexagonal.lib.utils.InsecureSaltedEncryptionUtil

import java.time.Instant
import java.util.UUID
import scala.concurrent.duration.*

private object RequestIdSettings {
  final val splitSeparator = ";"
}

final case class ExpiringValue(uuid: UUID, expiration: Instant)
    extends InsecureSaltedEncryptionUtil {
  private def toSplittableString =
    s"$uuid${RequestIdSettings.splitSeparator}$expiration"

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
  def apply(expirationDuration: Option[Duration] = Option.empty) =
    new ExpiringValue(
      UUID.randomUUID(),
      Instant.now().plusMillis(expirationDuration.getOrElse(5.minutes).toMillis)
    )

  def fromBase64Hmac(hashString: String): Option[ExpiringValue] = {
    try {
      val splitHash = decryptBase64Hmac(hashString)
        .split(RequestIdSettings.splitSeparator)
      Some(
        ExpiringValue(
          UUID.fromString(splitHash(0)),
          Instant.parse(splitHash(1))
        )
      )
    } catch {
      case _: Any => None
    }
  }

  final class ExpiringValueInvalid(message: String)
      extends Exception(message)
}
