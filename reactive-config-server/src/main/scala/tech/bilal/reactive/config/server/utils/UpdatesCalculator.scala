package tech.bilal.reactive.config.server.utils

import tech.bilal.reactive.config.server.models.ConfigFile.{
  EnvConfigFile,
  ServiceConfigFile
}
import tech.bilal.reactive.config.server.models.{
  ConfigFile,
  RegisteredService,
  Update
}

import scala.util.matching.Regex

object UpdatesCalculator {
  def updates(
      filePaths: Set[String],
      registeredServices: Set[RegisteredService]
  ): Set[Update] =
    configFiles(filePaths)
      .foldRight(Set.empty[Update]) { (cur, acc) =>
        cur match {
          case ServiceConfigFile(serviceName) =>
            val allEnvs = registeredServices
              .groupBy(_.serviceName)
              .getOrElse(serviceName, Set.empty)
              .map(_.env)
            acc + Update(serviceName, allEnvs)
          case EnvConfigFile(serviceName, env) =>
            val allEnvs = registeredServices
              .groupBy(_.serviceName)
              .getOrElse(serviceName, Set.empty)
              .map(_.env)
            acc + Update(serviceName, allEnvs.intersect(Set(env)))
        }
      }
      .groupBy(_.serviceName)
      .map {
        case (k, v) => Update(k, v.flatMap(_.envs))
      }
      .toSet
      .filter(_.envs.nonEmpty)

  def configFiles(filePaths: Set[String]): Set[ConfigFile] = {
    filePaths.map { filePath =>
      val serviceRegex: Regex = s"^(.*)\\.conf".r
      val envRegex: Regex = s"^(.*)%(.*)\\.conf".r
      filePath match {
        case envRegex(serviceName, envName) =>
          EnvConfigFile(serviceName, envName)
        case serviceRegex(serviceName) => ServiceConfigFile(serviceName)
      }
    }
  }
}
