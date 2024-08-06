package com.tomshley.hexagonal.reqreply.models

import com.tomshley.hexagonal.utils.InsecureSaltedEncryptionUtil

import java.time.Instant
import java.util.UUID
import scala.concurrent.duration.*

private object RequestIdSettings {
  final val splitSeparator = ";"
}

final case class IdempotentRequestId(uuid: UUID, expiration: Instant)
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

object IdempotentRequestId extends InsecureSaltedEncryptionUtil {
  def apply(expirationDuration: Option[Duration] = Option.empty) =
    new IdempotentRequestId(
      UUID.randomUUID(),
      Instant.now().plusMillis(expirationDuration.getOrElse(5.minutes).toMillis)
    )

  def fromBase64Hmac(hashString: String): Option[IdempotentRequestId] = {
    try {
      val splitHash = decryptBase64Hmac(hashString)
        .split(RequestIdSettings.splitSeparator)
      Some(
        IdempotentRequestId(
          UUID.fromString(splitHash(0)),
          Instant.parse(splitHash(1))
        )
      )
    } catch {
      case _: Any => None
    }
  }

  final class IdempotentRequestExpired(message: String)
      extends Exception(message)
}
