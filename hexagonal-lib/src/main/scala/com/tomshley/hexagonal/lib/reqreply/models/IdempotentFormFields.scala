package com.tomshley.hexagonal.lib.reqreply.models

import com.tomshley.hexagonal.lib.reqreply.forms.models.FormFieldNames

trait IdempotentFormFields extends FormFieldNames {
  val requestId: String = "request-id"

  lazy val idempotentRequestId: Option[IdempotentRequestId] = {
    IdempotentRequestId.fromBase64Hmac(requestId)
  }
  override def validFields: Seq[String] = super.validFields ++ Seq(
    requestId
  )
}

object IdempotentFormFields extends IdempotentFormFields

