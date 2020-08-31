package tech.bilal.reactive.config.server.utils

import java.util.UUID

import akka.Done
import akka.actor.typed.SpawnProtocol.{Command, Spawn}
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed._
import akka.util.Timeout

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.DurationInt

trait ConcurrentData[A] {
  def addAll(configs: Set[A]): Future[Done]
  def exists(config: A): Future[Boolean]
}

object ConcurrentData {

  def of[A](implicit
      actorSystem: ActorSystem[Command]
  ): ConcurrentData[A] = {

    class ConcurrentDataImpl(
        actorRefF: Future[ActorRef[ConcurrentDataCommands]]
    )(implicit
        timeout: Timeout,
        scheduler: Scheduler,
        executionContext: ExecutionContext
    ) extends ConcurrentData[A] {
      def addAll(configs: Set[A]): Future[Done] =
        actorRefF.flatMap(_ ? (AddAll(configs, _)))
      def exists(config: A): Future[Boolean] =
        actorRefF.flatMap(_ ? (Exists(config, _)))
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
        UUID.randomUUID().toString,
        Props.empty,
        _
      ))
    new ConcurrentDataImpl(actorRefF)
  }
}
