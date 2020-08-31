package tech.bilal.reactive.config.server.utils

import akka.Done
import akka.actor.typed.SpawnProtocol.{Command, Spawn}
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed._
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

trait ConcurrentData[A] {
  def addAll(configs: Set[A]): Future[Done]
  def exists(config: A): Future[Boolean]
}

object ConcurrentData {

  def of[A](implicit
      actorSystem: ActorSystem[Command]
  ): Future[ConcurrentData[A]] = {

    class ConcurrentDataImpl(actorRef: ActorRef[ConcurrentDataCommands])(
        implicit
        timeout: Timeout,
        scheduler: Scheduler
    ) extends ConcurrentData[A] {
      def addAll(configs: Set[A]): Future[Done] =
        actorRef ? (AddAll(configs, _))
      def exists(config: A): Future[Boolean] =
        actorRef ? (Exists(config, _))
    }

    sealed trait ConcurrentDataCommands
    case class AddAll(all: Set[A], replyTo: ActorRef[Done])
        extends ConcurrentDataCommands
    case class Exists(configParams: A, replyTo: ActorRef[Boolean])
        extends ConcurrentDataCommands

    def beh(state: Set[A]): Behavior[ConcurrentDataCommands] =
      Behaviors.receiveMessage[ConcurrentDataCommands] {
        case AddAll(allConfigParams, replyTo) =>
          replyTo ! Done
          beh(allConfigParams)
        case Exists(configParams, replyTo) =>
          replyTo ! state.contains(configParams)
          Behaviors.same
      }

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
    actorRefF.map(actorRef => new ConcurrentDataImpl(actorRef))
  }
}
