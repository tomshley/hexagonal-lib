package com.tomshley.brands.global.tech.tware.products.hexagonal.lib
package marshalling

import org.json4s._
import org.json4s.jackson.Serialization.{read, write}

import models.MarshallModel

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.Manifest

object JsonMarshaller {
  def serializeWithDefaults[T <: MarshallModel[T] : Manifest](model: T) : String = {
    implicit val formats = DefaultFormats
    write[T](model)
  }

  def deserializeWithDefaults[T <: MarshallModel[T] : Manifest](json: String): T = {
    implicit val formats = DefaultFormats
    read[T](json)
  }

  def serializeWithDefaultsAsync[T <: MarshallModel[T] : Manifest](model: T, ec: ExecutionContext) : Future[String] = {
    given ExecutionContext = ec
    implicit val formats = DefaultFormats
    Future { write[T](model) }
  }

  def deserializeWithDefaultsAsync[T <: MarshallModel[T] : Manifest](json: String, ec: ExecutionContext): Future[T] = {
    given ExecutionContext = ec
    implicit val formats = DefaultFormats
    Future { read[T](json) }
  }

  def serializeCustomMap(model: Map[String, Any]): String = {
    implicit val formats = DefaultFormats
    write(model)
  }

  def deserializeCustomMap(json: String): Map[String, Any] = {
    implicit val formats = DefaultFormats
    read(json)
  }
}
