package  com.tomshley.hexagonal.lib.reqreply.forms.models

import com.tomshley.hexagonal.lib.marshalling.models.MarshallModel

final case class NamedValidation(fieldName: String, message: String) extends MarshallModel[NamedValidation]
