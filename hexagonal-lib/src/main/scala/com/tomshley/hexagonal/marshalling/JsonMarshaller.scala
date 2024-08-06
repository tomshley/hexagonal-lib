package com.tomshley.hexagonal.marshalling

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
    implicit val formats = marshallerFormats
    write[T](model)
  }

  /** deserializeWithDefaults
   *
   * @param json String
   * @tparam T T <: MarshallModel[ T ]
   * @return T
   */
  final def deserializeWithDefaults[T <: MarshallModel[T] : Manifest](json: String): T = {
    implicit val formats = marshallerFormats
    read[T](json)
  }

  /** serializeWithDefaultsAsync
   *
   * @param model T
   * @tparam T T <: MarshallModel[ T ]
   * @return String
   */
  final def serializeWithDefaultsAsync[T <: MarshallModel[T] : Manifest](model: T, ec: ExecutionContext) : Future[String] = {
    implicit val exec: ExecutionContext = ec
    implicit val formats = marshallerFormats
    Future { write[T](model) }
  }

  /** deserializeWithDefaultsAsync
   *
   * @param json String
   * @tparam T T <: MarshallModel[ T ]
   * @return T
   */
  final def deserializeWithDefaultsAsync[T <: MarshallModel[T] : Manifest](json: String, ec: ExecutionContext): Future[T] = {
    implicit val exec: ExecutionContext = ec
    implicit val formats = marshallerFormats
    Future { read[T](json) }
  }

  final def serializeCustomMap(model: Map[String, Any]): String = {
    implicit val formats = marshallerFormats
    write(model)
  }

  final def deserializeCustomMap(json: String): Map[String, Any] = {
    implicit val formats = marshallerFormats
    read(json)
  }

}
object JsonMarshaller extends JsonMarshaller
