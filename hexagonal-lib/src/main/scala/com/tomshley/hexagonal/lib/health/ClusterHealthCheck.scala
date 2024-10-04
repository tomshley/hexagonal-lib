package com.tomshley.hexagonal.lib.health

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.cluster.{Cluster, MemberStatus}
import org.slf4j.LoggerFactory

import scala.concurrent.Future

class ClusterHealthCheck(system: ActorSystem) extends (() => Future[Boolean]) {
  private val log = LoggerFactory.getLogger(getClass)

  private val cluster = Cluster(system)
  override def apply(): Future[Boolean] = {
    log.info("com.tomshley.hexagonal.lib.health.ClusterHealthCheck called")

    Future.successful(cluster.selfMember.status == MemberStatus.Up)
  }
}
