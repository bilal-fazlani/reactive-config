import sbt._

object Libs {
  //AKKA
  lazy val `akka-http` = "com.typesafe.akka" %% "akka-http" % "10.2.0"
  lazy val `akka-actor-typed` =
    "com.typesafe.akka" %% "akka-actor-typed" % "2.6.8"
  lazy val `akka-stream` = "com.typesafe.akka" %% "akka-stream" % "2.6.8"

  //JSON
  lazy val `borer-core` = "io.bullet" %% "borer-core" % "1.6.1"
  lazy val `borer-derivation` = "io.bullet" %% "borer-derivation" % "1.6.1"
  lazy val `borer-akka` = "io.bullet" %% "borer-compat-akka" % "1.6.1"

  //CONFIG
  lazy val `typesafe-config` = "com.typesafe" % "config" % "1.4.0"
  lazy val pureconfig = "com.github.pureconfig" %% "pureconfig" % "0.13.0"

  //LOGGING
  lazy val slf4j = "org.slf4j" % "slf4j-simple" % "1.7.30"
}

object TestLibs {
  lazy val munit = "org.scalameta" %% "munit" % "0.7.10"
}
