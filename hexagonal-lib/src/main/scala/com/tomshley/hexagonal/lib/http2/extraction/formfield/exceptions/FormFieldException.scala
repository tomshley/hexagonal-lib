package com.tomshley.hexagonal.lib.http2.extraction.formfield.exceptions

import com.tomshley.hexagonal.lib.http2.extraction.formfield.exceptions.models.FormFieldExceptionSerializableEnvelope
import com.tomshley.hexagonal.lib.marshalling.JsonMarshaller

final case class FormFieldException(formFieldErrorValidationListEnvelope: FormFieldExceptionSerializableEnvelope,
                                    cause: Throwable = None.orNull)
  extends IllegalArgumentException(JsonMarshaller.serializeWithDefaults[FormFieldExceptionSerializableEnvelope](formFieldErrorValidationListEnvelope), cause)