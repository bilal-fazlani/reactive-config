package tech.bilal.reactive.config.server

import akka.Done
import akka.actor.typed.SpawnProtocol.{Command, Spawn}
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Props, Scheduler}
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.scaladsl.Source
import akka.util.Timeout
import io.bullet.borer.compat.akkaHttp._
import tech.bilal.reactive.config.server.webhook.models.PushHook

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

//case class ConfigParams(svc: String, env: String)

class ConcurrentDataActor[A] {
  sealed trait ConcurrentDataCommands
  case class AddAll(all: Set[A], replyTo: ActorRef[Done])
      extends ConcurrentDataCommands
  case class Exists(configParams: A, replyTo: ActorRef[Boolean])
      extends ConcurrentDataCommands

  class ConcurrentData(actorRef: ActorRef[ConcurrentDataCommands])(implicit
      timeout: Timeout,
      scheduler: Scheduler
  ) {
    def addAll(configs: Set[A]): Future[Done] =
      actorRef ? (AddAll(configs, _))
    def exists(config: A): Future[Boolean] =
      actorRef ? (Exists(config, _))
  }

  def beh(state: Set[A]): Behavior[ConcurrentDataCommands] =
    Behaviors.receiveMessage[ConcurrentDataCommands] {
      case AddAll(allConfigParams, replyTo) =>
        replyTo ! Done
        beh(allConfigParams)
      case Exists(configParams, replyTo) =>
        replyTo ! state.contains(configParams)
        Behaviors.same
    }

  def start(implicit
      actorSystem: ActorSystem[Command]
  ): Future[ConcurrentData] = {
    implicit val scheduler: Scheduler = actorSystem.scheduler
    implicit val timeout: Timeout = Timeout(2.seconds)
    import actorSystem.executionContext
    val actorRefF: Future[ActorRef[ConcurrentDataCommands]] =
      actorSystem ? (Spawn[ConcurrentDataCommands](
        beh(Set.empty[A]),
        "registration-actor",
        Props.empty,
        _
      ))
    actorRefF.map(actorRef => new ConcurrentData(actorRef))
  }
}

class AppRoutes extends Directives {
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
