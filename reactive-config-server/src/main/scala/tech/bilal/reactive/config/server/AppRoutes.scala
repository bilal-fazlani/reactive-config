package tech.bilal.reactive.config.server

import akka.actor.typed.ActorSystem
import akka.actor.typed.SpawnProtocol.Command
import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.scaladsl.Source
import io.bullet.borer.compat.akkaHttp._
import tech.bilal.reactive.config.server.utils.ConcurrentData
import tech.bilal.reactive.config.server.webhook.models.PushHook

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

case class ConfigParams(svc: String, env: String)

class AppRoutes(implicit actorSystem: ActorSystem[Command]) extends Directives {

  val concurrentData: Future[ConcurrentData[ConfigParams]] =
    ConcurrentData.of[ConfigParams]

  val routes: Route =
    (get & path("config") & parameter("service-name", "environment")) {
      (service, env) =>
        complete(
          Source
            .fromIterator(() => Iterator.from(1))
            .map(x => ServerSentEvent(s"Event: ${x.toString}"))
            .throttle(1, 1.second)
            .take(10)
        )
    } ~ (post & path("github")) {
      entity(as[PushHook]) { hook =>
        val files =
          hook.commits.flatMap(c => c.added ++ c.modified ++ c.removed).toSet
        println(files)
        complete("OK")
      }
    }
}
