package com.tomshley.hexagonal.lib.marshalling

import org.json4s.*
import org.json4s.jackson.Serialization.{read, write}

import models.MarshallModel

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.Manifest

trait JsonMarshaller {

  val marshallerFormats: Formats = DefaultFormats
  /** serializeWithDefaults
   *
   * @param model T
   * @tparam T T <: MarshallModel[ T ]
   * @return String
   */
  final def serializeWithDefaults[T <: MarshallModel[T] : Manifest](model: T) : String = {
    given formats: Formats = marshallerFormats
    write[T](model)
  }

  /** deserializeWithDefaults
   *
   * @param json String
   * @tparam T T <: MarshallModel[ T ]
   * @return T
   */
  final def deserializeWithDefaults[T <: MarshallModel[T] : Manifest](json: String): T = {
    given formats: Formats = marshallerFormats
    read[T](json)
  }

  /** serializeWithDefaultsAsync
   *
   * @param model T
   * @tparam T T <: MarshallModel[ T ]
   * @return String
   */
  final def serializeWithDefaultsAsync[T <: MarshallModel[T] : Manifest](model: T, ec: ExecutionContext) : Future[String] = {
    given exec: ExecutionContext = ec
    given formats: Formats = marshallerFormats
    Future { write[T](model) }
  }

  /** deserializeWithDefaultsAsync
   *
   * @param json String
   * @tparam T T <: MarshallModel[ T ]
   * @return T
   */
  final def deserializeWithDefaultsAsync[T <: MarshallModel[T] : Manifest](json: String, ec: ExecutionContext): Future[T] = {
    given exec: ExecutionContext = ec
    given formats: Formats = marshallerFormats
    Future { read[T](json) }
  }

  final def serializeCustomMap(model: Map[String, Any]): String = {
    given formats: Formats = marshallerFormats
    write(model)
  }

  final def deserializeCustomMap(json: String): Map[String, Any] = {
    given formats: Formats = marshallerFormats
    read(json)
  }

}
object JsonMarshaller extends JsonMarshaller
