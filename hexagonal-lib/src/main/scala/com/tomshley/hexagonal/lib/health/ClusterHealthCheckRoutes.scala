package com.tomshley.hexagonal.lib.health

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.cluster.{Cluster, MemberStatus}
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.model.{ContentTypes, HttpEntity, _}
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.{Route, StandardRoute}
import org.apache.pekko.stream.Materializer

import scala.io.StdIn

final class ClusterHealthCheckRoutes(host: String, port: Int)(implicit system: ActorSystem) {

  private val cluster = Cluster(system)

  private def ready: StandardRoute = {
    val status = cluster.selfMember.status
    if (status == MemberStatus.Up || status == MemberStatus.WeaklyUp)
      complete(
        HttpEntity(ContentTypes.`application/json`, s"""{ "heatlh": "OK" }"""))
    else
      complete(StatusCodes.NotFound)
  }

  private val route: Route =
    path("ready") {
      get {
        ready
      }
    } ~
      path("alive") {
        get {
          ready
        }
      }

  def bootHealthCheck()(implicit mat: Materializer): Unit = {
    val bindingFuture = Http().bindAndHandle(route, host, port)
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind())(system.dispatcher) // trigger unbinding from the port
      .onComplete(_ => system.terminate())(system.dispatcher) // and shutdown when done
  }

}
