package com.tomshley.hexagonal.lib.http2.extraction.formfield.exceptions.models

import com.tomshley.hexagonal.lib.http2.extraction.formfield.models.NamedValidation
import com.tomshley.hexagonal.lib.marshalling.models.MarshallModel

final case class FormFieldErrorValidationListEnvelope(errors: List[NamedValidation]) extends MarshallModel[FormFieldErrorValidationListEnvelope]
