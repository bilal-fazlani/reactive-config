package tech.bilal.reactive.config.server

import akka.actor.typed.{ActorSystem, SpawnProtocol}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

object Main extends App with Directives {
  val defaultPort = 8080

  val port = sys.env
    .get("SERVER_PORT")
    .flatMap(_.toIntOption)
    .getOrElse(defaultPort)

  implicit val actorSystem = ActorSystem(SpawnProtocol(), "main")

  val routes = new AppRoutes().routes

  val binding = Await.result(
    Http()
      .newServerAt("0.0.0.0", port)
      .bind(routes),
    5.seconds
  )

  println(s"server started at http://localhost:$port")
}
