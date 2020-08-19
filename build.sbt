inThisBuild(
  Seq(
    scalaVersion := "2.13.3",
    organization := "tech.bilal",
    homepage := Some(
      url("https://github.com/bilal-fazlani/reactive-config")
    ),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/bilal-fazlani/reactive-config"),
        "git@github.com:bilal-fazlani/reactive-config.git"
      )
    ),
    developers := List(
      Developer(
        "bilal-fazlani",
        "Bilal Fazlani",
        "bilal.m.fazlani@gmail.com",
        url("https://bilal-fazlani.com")
      )
    ),
    licenses += ("MIT", url(
      "https://github.com/bilal-fazlani/reactive-config/blob/master/LICENSE"
    )),
    crossPaths := true,
    Test / parallelExecution := false,
    testFrameworks += new TestFramework("munit.Framework")
  )
)

lazy val `reactive-config-root` = project
  .in(file("."))
  .settings(
    name := "reactive-config-root"
  )
  .aggregate(`reactive-config-server`)

lazy val `reactive-config-server` = project
  .in(file("./reactive-config-server"))
  .settings(
    name := "reactive-config-server",
    libraryDependencies ++= Seq(
      Libs.`akka-actor-typed`,
      Libs.`akka-stream`,
      Libs.`akka-http`,
      Libs.`borer-core`,
      Libs.`borer-derivation`,
      Libs.`borer-akka`,
      Libs.slf4j,
      TestLibs.munit % Test
    )
  )
