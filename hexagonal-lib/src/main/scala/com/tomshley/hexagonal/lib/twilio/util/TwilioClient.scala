package com.tomshley.hexagonal.lib.twilio.util

import com.twilio.Twilio
import com.twilio.`type`.PhoneNumber
import com.twilio.rest.api.v2010.account.Message
import org.apache.pekko.Done
import org.apache.pekko.actor.typed.ActorSystem

import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.jdk.FutureConverters.*

object TwilioClient {
  private val configInstance: Promise[TwilioConfig] = Promise()

  private def configInstanceMaybe = configInstance.future.value.flatMap(_.toOption)

  @tailrec
  final def init(twilioConfig: TwilioConfig): TwilioConfig = {
    configInstanceMaybe match
      case Some(value) => value
      case None =>
        configInstance.trySuccess {
          twilioConfig
        }
        init(twilioConfig)
  }

  def sendMessageAsync(system: ActorSystem[?], to: String, body: String): Future[Message] = {
    given ec: ExecutionContext = system.executionContext

    if configInstanceMaybe.isEmpty then {
      Future.failed(new Exception("Twilio is not initialized"))
    } else {
      Twilio.init(configInstanceMaybe.get.accountSid, configInstanceMaybe.get.authToken)
      Message
        .creator(
          new PhoneNumber(to),
          configInstanceMaybe.get.twilioFrom,
          body
        ).createAsync().asScala
    }
  }

  def sendMessage(system: ActorSystem[?], to: String, body: String): Option[Message] = {
    if configInstanceMaybe.isEmpty then {
      Option.empty[Message]
    } else {
      Twilio.init(configInstanceMaybe.get.accountSid, configInstanceMaybe.get.authToken)
      Some(Message
        .creator(
          new PhoneNumber(to),
          configInstanceMaybe.get.twilioFrom,
          body
        ).create())
    }
  }
}


