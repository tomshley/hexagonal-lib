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
package cloudevents

import java.net.URI
import java.time.ZonedDateTime
import scala.collection.mutable


// Based on https://github.com/cloudevents/sdk-java/blob/master/core/src/main/java/io/cloudevents/core/v1/CloudEventV1.java
// and https://github.com/cloudevents/sdk-java/blob/master/core/src/main/java/io/cloudevents/core/impl/BaseCloudEvent.java
// Json definition is here https://github.com/cloudevents/spec/blob/master/spec.json

/*
 *    "id": "description": "Identifies the event.", "examples": ["A234-1234-1234"]
 *    "source": "description": "Identifies the context in which an event happened.","examples" : [
 *       "https://github.com/cloudevents",
 *       "mailto:cncf-wg-serverless@lists.cncf.io",
 *       "urn:uuid:6e8bc430-9c3a-11d9-9669-0800200c9a66",
 *       "cloudevents/spec/pull/123",
 *       "/sensors/tn-1234567/alerts",
 *       "1-555-123-4567"]
 *    "specversion": "description": "The version of the CloudEvents specification which the event uses.","examples": ["1.x-wip"]
 *    "type": "description": "Describes the type of event related to the originating occurrence.","examples" : [
 *        "com.github.pull_request.opened",
 *        "com.example.object.deleted.v2"]
 *    "datacontenttype": "description": "Content type of the data value. Must adhere to RFC 2046 format.", "examples": [
 *        "text/xml",
 *        "application/json",
 *        "image/png",
 *           },
 *    "dataschema": "description": "Identifies the schema that data adheres to.",
 *    "subject": "description": "Describes the subject of the event in the context of the event producer (identified by source).","examples": ["mynewfile.jpg"]
 *    "time": "description": "Timestamp of when the occurrence happened. Must adhere to RFC 3339.", "examples": ["2018-04-05T17:31:00Z"]
 *    "data": "description": "The event payload.", "examples": ["<much wow=\"xml\"/>"]}
 *    "data_base64": "description": "Base64 encoded event payload. Must adhere to RFC4648.","examples": ["Zm9vYg=="]
 *
 *    "required": ["id", "source", "specversion", "type"]
 *
 *    To allow some of the variables to come from the header we make all of the variables here optional
 */

protected[hexagonal] case class CloudEvent(
                                            var id: String,
                                            var source: URI,
                                            var specversion: String,
                                            var `type`: String,
                                            var datacontenttype: Option[String],
                                            var dataschema: Option[URI],
                                            var subject: Option[String],
                                            var time: Option[ZonedDateTime],
                                            var data: Option[String],
                                            var data_base64: Option[Array[Byte]],
                                            var extensions: Option[Map[String, Any]]
                                          ) {

  override def toString: String = {
    val builder = new mutable.StringBuilder()
    builder
      .append("CloudEvent{")
      .append(s"id=$id,")
      .append(s" source=${source.toString},")
      .append(s" specversion=$specversion,")
      .append(s" type=${`type`},")
    datacontenttype match {
      case Some(d) => builder.append(s" datacontenttype = $d,")
      case _ =>
    }
    dataschema match {
      case Some(d) => builder.append(s" dataschema = ${d.toString},")
      case _ =>
    }
    subject match {
      case Some(s) => builder.append(s" subject=$s,")
      case _ =>
    }
    time match {
      case Some(t) => builder.append(s" time=$t,")
      case _ =>
    }
    data match {
      case Some(d) => builder.append(s" data=$d,")
      case _ =>
    }
    data_base64 match {
      case Some(d) => builder.append(s" data=$d,")
      case _ =>
    }
    extensions match {
      case Some(e) => builder.append(s" extensions=$e")
      case _ =>
    }
    builder.append("}")
    builder.toString()
  }
}
