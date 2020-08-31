package tech.bilal.reactive.config.server

import java.nio.file.Path

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.scaladsl.FileIO
import akka.util.ByteString
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

object MergingPOC extends App {
  implicit val actorSystem: ActorSystem[_] =
    ActorSystem(Behaviors.empty, "main")
  import actorSystem.executionContext

  val fileRootDir = "/Users/bilal/projects/scala/config-sample/"
  val base = fileRootDir + "service1.conf"
  val dev = fileRootDir + "service1-dev.conf"

  val baseConfig = ConfigFactory.parseString(readFile(base))
  val devConfig = ConfigFactory.parseString(readFile(dev))

  val c = devConfig.withFallback(baseConfig).resolve()

  println(c)

  def readFile(path: String): String =
    block(
      FileIO
        .fromPath(Path.of(path))
        .runFold(ByteString.empty)(_ ++ _)
        .map(_.utf8String)
    )
  def block[A](f: Future[A]) = Await.result(f, 10.seconds)
}
