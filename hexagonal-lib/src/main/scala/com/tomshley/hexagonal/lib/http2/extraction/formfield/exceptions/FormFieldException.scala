package com.tomshley.hexagonal.lib.http2.extraction.formfield.exceptions

import com.tomshley.hexagonal.lib.http2.extraction.formfield.exceptions.models.FormFieldErrorValidationListEnvelope
import com.tomshley.hexagonal.lib.marshalling.JsonMarshaller

final case class FormFieldException(formFieldErrorValidationListEnvelope: FormFieldErrorValidationListEnvelope,
                                    cause: Throwable = None.orNull)
  extends IllegalArgumentException(JsonMarshaller.serializeWithDefaults[FormFieldErrorValidationListEnvelope](formFieldErrorValidationListEnvelope), cause)