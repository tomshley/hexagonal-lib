/*
 * copyright 2023 tomshley llc
 *
 * licensed under the apache license, version 2.0 (the "license");
 * you may not use this file except in compliance with the license.
 * you may obtain a copy of the license at
 *
 * http://www.apache.org/licenses/license-2.0
 *
 * unless required by applicable law or agreed to in writing, software
 * distributed under the license is distributed on an "as is" basis,
 * without warranties or conditions of any kind, either express or implied.
 * see the license for the specific language governing permissions and
 * limitations under the license.
 *
 * @author thomas schena @sgoggles <https://github.com/sgoggles> | <https://gitlab.com/sgoggles>
 *
 */

package com.tomshley.brands.global.tech.tware.products.hexagonal.lib
package marshalling

import org.json4s.*
import org.json4s.jackson.Serialization.{read, write}

import models.MarshallModel

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.Manifest

trait JsonMarshaller {
  def serializeWithDefaults[T <: MarshallModel[T] : Manifest](model: T)(implicit formats:Formats = DefaultFormats) : String = {
    write[T](model)
  }

  def deserializeWithDefaults[T <: MarshallModel[T] : Manifest](json: String)(implicit formats:Formats = DefaultFormats): T = {
    read[T](json)
  }

  def serializeWithDefaultsAsync[T <: MarshallModel[T] : Manifest](model: T, ec: ExecutionContext)(implicit formats:Formats = DefaultFormats) : Future[String] = {
    given ExecutionContext = ec
    Future { write[T](model) }
  }

  def deserializeWithDefaultsAsync[T <: MarshallModel[T] : Manifest](json: String, ec: ExecutionContext)(implicit formats:Formats = DefaultFormats): Future[T] = {
    given ExecutionContext = ec
    Future { read[T](json) }
  }

  def serializeCustomMap(model: Map[String, Any])(implicit formats:Formats = DefaultFormats): String = {
    write(model)
  }

  def deserializeCustomMap(json: String)(implicit formats:Formats = DefaultFormats): Map[String, Any] = {
    read(json)
  }
}

object JsonMarshaller extends JsonMarshaller
