package tech.bilal.reactive.config.server

import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.scaladsl.Source
import io.bullet.borer.compat.akkaHttp._
import tech.bilal.reactive.config.server.webhook.models.PushHook

import scala.concurrent.duration.DurationInt

class AppRoutes extends Directives {
  val routes: Route = (get & path("config")) {
    complete(
      Source
        .fromIterator(() => Iterator.from(1))
        .map(x => ServerSentEvent(s"Event: ${x.toString}"))
        .throttle(1, 1.second)
        .take(10)
    )
  } ~ (post & path("github")) {
    entity(as[PushHook]) { hook =>
      println(hook.toString)
      complete("OK")
    }
  }
}
