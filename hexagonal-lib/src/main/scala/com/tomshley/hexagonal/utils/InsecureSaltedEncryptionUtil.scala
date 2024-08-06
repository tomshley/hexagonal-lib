package com.tomshley.hexagonal.utils

import com.typesafe.config.ConfigFactory
import org.apache.commons.codec.binary.Base64

import java.util
import javax.crypto.spec.SecretKeySpec
import javax.crypto.{Cipher, Mac}

private object SaltedHmacSettings extends ConfigKeyUtil{
  final val cipherAlgorithm = "AES/ECB/PKCS5Padding"
  final val algorithm = "AES"
  final val insecureSalt = {
    new SecretKeySpec(
      util.Arrays.copyOf(
        config
          .getString("tomshley-hexagonal-reqreply-idempotency.insecure-salt")
          .getBytes("UTF-8"),
        16
      ),
      algorithm
    )
  }
}
sealed trait EncryptDecrypt {
  def encrypt(value: String): Array[Byte]
  
  def decrypt(byteArray: Array[Byte]): String
}

protected class InsecureSaltedEncryption extends EncryptDecrypt {
  private def getCipherInstance(cipherMode: Int): Cipher = {
    val cipher = Cipher.getInstance(SaltedHmacSettings.cipherAlgorithm)
    cipher.init(cipherMode, SaltedHmacSettings.insecureSalt)
    cipher
  }

  def encrypt(value: String): Array[Byte] = {
    getCipherInstance(Cipher.ENCRYPT_MODE).doFinal(value.getBytes("UTF-8"))
  }
  
  def decrypt(byteArray: Array[Byte]): String = {
    new String(getCipherInstance(Cipher.DECRYPT_MODE).doFinal(byteArray))
  }

}

trait InsecureSaltedEncryptionUtil extends EncryptDecrypt {
  override def encrypt(value: String): Array[Byte] = {
    InsecureSaltedEncryption().encrypt(value)
  }

  override def decrypt(byteArray: Array[Byte]): String =
    InsecureSaltedEncryption().decrypt(byteArray)
  
  def encryptBase64Hmac(value: String): String = {
    Base64.encodeBase64String(encrypt(value))
  }
  
  def decryptBase64Hmac(encodedValue: String): String = {
    val decoded = Base64.decodeBase64(encodedValue)
    decrypt(decoded)
  }
}