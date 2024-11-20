package com.tomshley.hexagonal.lib.health

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.cluster.{Cluster, MemberStatus}
import org.slf4j.LoggerFactory

import scala.concurrent.Future

class HealthCheck(system: ActorSystem) extends (() => Future[Boolean]) {
  private val log = LoggerFactory.getLogger(getClass)

  override def apply(): Future[Boolean] = {
    log.info("com.tomshley.hexagonal.lib.health.HealthCheck called")
    Future.successful(true)
  }
}
