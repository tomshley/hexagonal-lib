package  com.tomshley.hexagonal.lib.reqreply.forms.exceptions.models

import com.tomshley.hexagonal.lib.marshalling.models.MarshallModel
import com.tomshley.hexagonal.lib.reqreply.forms.models.NamedValidation

final case class FormFieldErrorValidationListEnvelope(errors: List[NamedValidation]) extends MarshallModel[FormFieldErrorValidationListEnvelope]
