package tech.bilal.reactive.config.server

import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.scaladsl.Source
import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
import akka.http.scaladsl.model.sse.ServerSentEvent

import scala.concurrent.duration.DurationInt

object Routes extends Directives {
  def value: Route =
    get {
      complete(
        Source
          .fromIterator(() => Iterator.from(1))
          .map(x => ServerSentEvent(s"Event: ${x.toString}"))
          .throttle(1, 1.second)
      )
    }
}
