package tech.bilal.reactive.config.server.utils

import akka.actor.ClassicActorSystemProvider
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.http.scaladsl.model.StatusCodes.OK
import scala.compat.java8.FutureConverters.CompletionStageOps
import scala.concurrent.{ExecutionContext, Future}

class GithubFileFetcher(githubRepo: String, githubOwner: String)(implicit
    actorSystemProvider: ClassicActorSystemProvider,
    executionContext: ExecutionContext
) {

  def file(path: String, sha: String): Future[String] = {
    val safePath = if (path.startsWith("/")) path.substring(1) else path
    Http()
      .singleRequest(
        Get(
          s"https://raw.githubusercontent.com/$githubOwner/$githubRepo/$sha/$safePath"
        )
      )
      .flatMap(x =>
        x.status match {
          case OK =>
            x.entity
              .getDataBytes()
              .runReduce(_ ++ _, actorSystemProvider)
              .toCompletableFuture
              .toScala
              .map(_.utf8String)
          case err =>
            Future.failed(
              new RuntimeException(s"http call failed with status code: $err")
            )
        }
      )
  }
}
