package com.tomshley.hexagonal.lib.http2.extraction.formfield

import com.tomshley.hexagonal.lib.http2.extraction.formfield.exceptions.FormFieldException
import com.tomshley.hexagonal.lib.http2.extraction.formfield.exceptions.models.FormFieldErrorValidationListEnvelope
import com.tomshley.hexagonal.lib.http2.extraction.formfield.models.GroupedRequireEnvelope

trait GroupedRequirements {
  @inline final def require(requirements: List[GroupedRequireEnvelope]): Unit = {
    val errorsEnvelope = FormFieldErrorValidationListEnvelope(requirements.filter(!_.condition).map(_.errorValidation).toList)
    if (errorsEnvelope.errors.nonEmpty) {
      throw FormFieldException(errorsEnvelope)
    }
  }
}