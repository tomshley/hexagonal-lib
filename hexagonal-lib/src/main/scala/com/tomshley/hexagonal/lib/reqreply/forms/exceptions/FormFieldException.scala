package  com.tomshley.hexagonal.lib.reqreply.forms.exceptions

import com.tomshley.hexagonal.lib.marshalling.JsonMarshaller
import com.tomshley.hexagonal.lib.reqreply.forms.exceptions.models.FormFieldErrorValidationListEnvelope

final case class FormFieldException(formFieldErrorValidationListEnvelope: FormFieldErrorValidationListEnvelope,
                                    cause: Throwable = None.orNull)
  extends IllegalArgumentException(JsonMarshaller.serializeWithDefaults[FormFieldErrorValidationListEnvelope](formFieldErrorValidationListEnvelope), cause)