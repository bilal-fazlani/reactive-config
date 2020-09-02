package tech.bilal.reactive.config.server.models

sealed trait ConfigFile
object ConfigFile {
  case class ServiceConfigFile(serviceName: String) extends ConfigFile
  case class EnvConfigFile(serviceName: String, env: String) extends ConfigFile
}
