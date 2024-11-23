package com.tomshley.hexagonal.lib.http2.extraction.formfield

import com.tomshley.hexagonal.lib.http2.extraction.formfield.exceptions.FormFieldException
import com.tomshley.hexagonal.lib.http2.extraction.formfield.exceptions.models.FormFieldExceptionSerializableEnvelope
import com.tomshley.hexagonal.lib.http2.extraction.formfield.models.GroupedRequireEnvelope

trait GroupedRequirements {
  @inline final def require(requirements: List[GroupedRequireEnvelope]): Unit = {
    val errorsEnvelope = FormFieldExceptionSerializableEnvelope(requirements.filter(!_.condition).map(_.errorValidation).toList)
    if (errorsEnvelope.errors.nonEmpty) {
      throw FormFieldException(errorsEnvelope)
    }
  }
}