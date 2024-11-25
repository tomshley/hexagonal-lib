package  com.tomshley.hexagonal.lib.reqreply.forms

import com.tomshley.hexagonal.lib.marshalling.JsonMarshaller
import com.tomshley.hexagonal.lib.reqreply.forms.exceptions.FormFieldException
import com.tomshley.hexagonal.lib.reqreply.forms.exceptions.models.FormFieldErrorValidationListEnvelope
import com.tomshley.hexagonal.lib.reqreply.forms.models.{NamedValidation, ValidFormFieldNames}
import org.apache.pekko.http.scaladsl.model.*
import org.apache.pekko.http.scaladsl.model.StatusCodes.*
import org.apache.pekko.http.scaladsl.server.ValidationRejection

sealed trait RejectionToErrorEnvelope {
  protected def errorEnvelopeFromValidationRejections[T <: ValidFormFieldNames](formFieldNames: T, validationRejections: Seq[ValidationRejection]): ErrorEnvelope = {
    ErrorEnvelope(validationRejections
      .filter(
        _.cause match
          case Some(FormFieldException(_, _)) =>
            true
          case _ =>
            false
      )
      .map(rejection =>
        try {
          Some(JsonMarshaller.deserializeWithDefaults[FormFieldErrorValidationListEnvelope](rejection.message))
        } catch {
          case _ =>
            Option.empty[FormFieldErrorValidationListEnvelope]
        }
      )
      .filter(errorValidationMaybe => {
        errorValidationMaybe.isDefined
      }).flatMap(errorValidationMaybe => {
        errorValidationMaybe.get.errors
      })
      .filter(formFieldErrorValidation => {
        formFieldNames.validFields.contains(formFieldErrorValidation.fieldName)
      })
      .foldLeft(Map.empty[String, List[String]]) { case (errorMap: Map[String, List[String]], formFieldErrorValidation: NamedValidation) =>
        errorMap + (formFieldErrorValidation.fieldName -> (errorMap.getOrElse(formFieldErrorValidation.fieldName, List.empty[String]) :+ formFieldErrorValidation.message))
      },
      validationRejections
        .filter(
          _.cause match
            case Some(FormFieldException(_, _)) => false
            case _ => true
        )
        .map(rejection => rejection.message).toList)
  }
}

final case class ErrorEnvelope(
                                fieldErrors: Map[String, List[String]],
                                nonFieldErrors: List[String]
                              )

object ErrorEnvelope extends RejectionToErrorEnvelope {
  def apply[T <: ValidFormFieldNames](formFieldNames: T, validationRejections: Seq[ValidationRejection]): ErrorEnvelope = {
    errorEnvelopeFromValidationRejections(formFieldNames, validationRejections)
  }
}
