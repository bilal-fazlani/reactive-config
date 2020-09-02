package tech.bilal.reactive.config.server

//
//import akka.actor.typed.SpawnProtocol.{Command, Spawn}
//import akka.actor.typed.scaladsl.Behaviors
//import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
//import akka.util.ByteString
//import tech.bilal.reactive.config.server.ServiceActor.ServiceActorMessage
//import tech.bilal.reactive.config.server.ServiceEnvActor.ServiceEnvActorMessage
//
//import scala.concurrent.{ExecutionContext, Future}
//
//object ServiceEnvActor {
//  case class State(commonFile: Seq[ByteString], file: Seq[ByteString])
//  sealed trait ServiceEnvActorMessage
//}
//
//object ServiceActor {
//  case class State(
//      file: Seq[ByteString],
//      children: Map[String, ActorRef[ServiceEnvActorMessage]]
//  )
//  sealed trait ServiceActorMessage
//
//}
//
//trait ConfigStore {
//  def addOrUpdateFiles(filePaths: Set[String])
//  def deleteFiles(filePaths: Set[String])
//
//  def configFor(service: String, env: String)
//}
//
//object ConfigStore {
//  sealed trait ConfigStoreCommand
//  case class AddOrUpdate(filePaths: Set[String]) extends ConfigStoreCommand
//  case class Delete(filePaths: Set[String]) extends ConfigStoreCommand
//
//  class ConfigStoreImpl(actorRefF: Future[ActorRef[ConfigStoreCommand]])(
//      implicit executionContext: ExecutionContext
//  ) {
//    def addOrUpdate(filePaths: Set[String]): Unit =
//      actorRefF.map(_ ! AddOrUpdate(filePaths))
//    def delete(filePaths: Set[String]): Unit =
//      actorRefF.map(_ ! Delete(filePaths))
//  }
//
//  case class State(children: Map[String, ActorRef[ServiceActorMessage]])
//
//  private def beh(state: State) =
//    Behaviors.receiveMessage[ConfigStoreCommand] {
//      case AddOrUpdate(filePaths) =>
//        val services:Set[String]
//      case Delete(filePaths)      =>
//    }
//
//  def start(implicit
//      actorSystem: ActorSystem[Command]
//  ): Future[ActorRef[ConfigStoreCommand]] = {
//    val actorRefF = actorSystem ? (Spawn())
//    ???
//  }
//}
//
