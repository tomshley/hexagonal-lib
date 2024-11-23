package com.tomshley.hexagonal.lib.http2.extraction.formfield.models

import com.tomshley.hexagonal.lib.marshalling.models.MarshallModel

case class NamedValidation(fieldName: String, message: String) extends MarshallModel[NamedValidation]
