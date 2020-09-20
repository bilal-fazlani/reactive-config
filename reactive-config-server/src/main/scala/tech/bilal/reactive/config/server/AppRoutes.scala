package tech.bilal.reactive.config.server

import akka.actor.typed.ActorSystem
import akka.actor.typed.SpawnProtocol.Command
import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.scaladsl.Source
import io.bullet.borer.compat.akkaHttp._
import tech.bilal.reactive.config.server.models.RegisteredService
import tech.bilal.reactive.config.server.utils.ConcurrentData
import tech.bilal.reactive.config.server.webhook.models.PushHook

import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

class AppRoutes(implicit actorSystem: ActorSystem[Command]) extends Directives {

  val registeredServices: ConcurrentData[RegisteredService] =
    ConcurrentData.of[RegisteredService]

  val routes: Route =
    (get & path("config" / Segment / Segment)) { (service, env) =>
      onComplete(registeredServices.exists(RegisteredService(service, env))) {
        case Success(true) =>
          complete(
            Source
              .fromIterator(() => Iterator.from(1))
              .map(x => ServerSentEvent(s"Event: ${x.toString}"))
              .throttle(1, 1.second)
              .take(10)
          )
        case Success(false)     => complete(StatusCodes.NotFound)
        case Failure(exception) => throw exception
      }

    } ~ (post & path("github")) {
      entity(as[PushHook]) { hook =>
        val addedOrUpdated = {
          hook.commits.flatMap(c => c.added ++ c.modified).toSet
        }
        val removed = hook.commits.flatMap(_.removed).toSet

        println(s"""
             |addedOrUpdated
             |------------------
             |${addedOrUpdated.mkString("\n")}
             |
             |removed
             |------------------
             |${removed.mkString("\n")}
             |""".stripMargin)
        complete("OK")
      }
    }
}
