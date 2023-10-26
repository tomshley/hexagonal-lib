/*
 * Copyright 2023 Tomshley LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.

 * @author Thomas Schena @sgoggles <https://github.com/sgoggles> | <https://gitlab.com/sgoggles>
 */

package com.tomshley.brands.global.tech.tware.products.hexagonal.lib
package runmainasfuture

import akka.Done
import com.tomshley.brands.global.tech.tware.products.hexagonal.lib.simplelogger.SLogger

import java.util.concurrent.Executors
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn

/** This is the primary abstract class for the server implementation that will live inside a future
 * Example: import io.grpc.ServerServiceDefinition import
 * com.tomshley.brands.global.tech.tware.examples.hexagonal.lib.hellogrpc.app.HelloWorldServiceHandler import
 * com.tomshley.brands.global.tech.tware.examples.hexagonal.lib.hellogrpc.helloworld.HelloWorldGrpc import
 * com.tomshley.brands.global.tech.tware.products.hexagonal.lib.runmainasfuture.{ GrpcServer, ServerProperties }
 *
 * import scala.concurrent.Future
 *
 * object HelloWorldServer2 extends GrpcServer {
 *
 * override lazy val serverProperties = ServerProperties(hostname = "localhost", port = 50051)
 *
 * addServices(HelloWorldGrpc.bindService(new HelloWorldServiceHandler(), ec))
 *
 * run }
 *
 * [S] is for the class of a service definition, like gRPCs ServiceDefinition [V] is for the class
 * type of the server, like io.grpc.Server
 */
protected[runmainasfuture] trait RunMainFutureSugar[S1, S2] extends App with SLogger {
  final lazy val run: Unit = {
    running.failed.foreach { ex =>
      logger.error(
        "there was an error",
        ex.asInstanceOf[Exception]
      )
    }
    running.onComplete(_ =>
      serverTermination.onComplete(_ =>
        logger.debug(
          "everything completed, yo/z"
        )
      )
    )
  }
  lazy val serverProperties: ServerProperties = {
    new ServerProperties()
  }
  lazy val serverCreation: Future[S1]
  lazy val serverTermination: Future[Done]
  /** This will return the future run of the main server based on std in
   */
  private[this] lazy val running: Future[Done] = {
    logger.debug("run called")
    serverCreation.failed.foreach { ex =>
      logger.error("there was an error", ex.asInstanceOf[Exception])
    }

    serverInfo
    StdIn.readLine()

    Future(akka.Done)
  }
  private[this] lazy val serverInfo: String = {
    lazy val stringBuilder: mutable.StringBuilder = new mutable.StringBuilder()
    stringBuilder.append("Server now online.")
    stringBuilder.append("Please navigate to: ")
    stringBuilder.append("https://")
    stringBuilder.append(serverProperties.hostname)
    stringBuilder.append(":")
    stringBuilder.append(serverProperties.port)
    stringBuilder.append("\n")
    stringBuilder.append("Press RETURN key to stop")

    lazy val value = stringBuilder.toString()
    logger.debug(value)
    value
  }

  given ec: ExecutionContext =
    ExecutionContext.fromExecutor(Executors.newFixedThreadPool(serverProperties.threadCount))

  /**
   * @param services
   */
  def addServices(services: S2*): Unit = {
    throw new NotImplementedError(
      "This method must be implemented to use services. Ok if not using services"
    )
  }
}
