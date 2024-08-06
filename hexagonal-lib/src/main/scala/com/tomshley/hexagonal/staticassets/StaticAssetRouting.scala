package com.tomshley.hexagonal.staticassets

import com.tomshley.hexagonal.staticassets.exceptions.StaticAssetRoutingRejection
import com.tomshley.hexagonal.utils.FilesUtil
import org.apache.pekko.actor
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.http.javadsl.{model, server}
import org.apache.pekko.http.scaladsl.model.*
import org.apache.pekko.http.scaladsl.server.Directives.{RemainingPath, complete, extractExecutionContext, onComplete, path, pathPrefix, reject}
import org.apache.pekko.http.scaladsl.server.{Directives, RejectionWithOptionalCause, Route}
import org.apache.pekko.stream.scaladsl.{Concat, Flow, Source, StreamConverters}
import org.apache.pekko.util.ByteString

import scala.util.{Failure, Success}

trait StaticAssetRouting extends FilesUtil {
  private def matchFileExtension(path: String): Option[StaticAssetType] = {
    StaticAssetType.values
      .find(assetType => {
        s".${assetType.toExtension}" == nameAndExtensionPair(path)(1)
      })
  }
  def getStaticAssetRoute(system: ActorSystem[?]): Route =
    Directives.get {
      pathPrefix("static") {
        path(RemainingPath) { pastePathWithExt =>
          {
            val matchedHTTPAssetType =
              matchFileExtension(pastePathWithExt.toString)

            if (matchedHTTPAssetType.isEmpty) {
              reject(
                StaticAssetRoutingRejection(
                  "An unsupported asset type specified"
                )
              )
            }
            extractExecutionContext { implicit executor =>
              {
                onComplete {
                  implicit val materializer: actor.ActorSystem =
                    system.classicSystem

                  /*
                  Note: this will eventually support concating files
                   */
                  val requestedFiles = Seq(s"$pastePathWithExt")
                  Source
                    .combine(
                      requestedFiles
                        .map {
                          getClass.getClassLoader.getResource(_)
                        }
                        .map(
                          pathUrl =>
                            StreamConverters
                              .fromInputStream(() => pathUrl.openStream())
                        )
                        .map(
                          inputSource =>
                            inputSource
                              .via(Flow[ByteString].map(_.map(_.toChar.toByte)))
                        )
                    )(Concat[ByteString])
                    .runReduce(_ ++ _)
                } {
                  case Failure(exception) =>
                    reject(
                      StaticAssetRoutingRejection(
                        "An error occurred reading the file"
                      )
                    )
                  case Success(value: ByteString) =>
                    complete(
                      HttpResponse(
                        entity = HttpEntity(
                          matchedHTTPAssetType.get.toContentType,
                          value.utf8String
                        )
                      )
                    )
                }
              }
            }
          }
        }
      }
    }
}
