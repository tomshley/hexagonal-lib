package  com.tomshley.hexagonal.lib.reqreply.forms.models

import com.tomshley.hexagonal.lib.marshalling.models.MarshallModel

final case class GroupedRequireEnvelope(condition: Boolean, errorValidation: NamedValidation) extends MarshallModel[GroupedRequireEnvelope]

