package com.tomshley.hexagonal.lib.http2.extraction.formfield.exceptions.models

import com.tomshley.hexagonal.lib.http2.extraction.formfield.models.NamedValidation
import com.tomshley.hexagonal.lib.marshalling.models.MarshallModel

case class FormFieldExceptionSerializableEnvelope(errors: List[NamedValidation]) extends MarshallModel[FormFieldExceptionSerializableEnvelope]
