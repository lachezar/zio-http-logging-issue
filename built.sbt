val scala3 = "3.1.3"
val allScala = Seq(scala3)

inThisBuild(
  List(
    scalaVersion := scala3,
    crossScalaVersions := allScala
  )
)

name := "zio-http-logging-issue"

lazy val root = project
  .in(file("."))
  .settings(crossScalaVersions := allScala)
  .settings(commonSettings)
  .settings(libraryDependencies ++= rootDependencies)

lazy val commonSettings = Def.settings(
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding",
    "UTF-8",
    "-feature",
    "-language:higherKinds",
    "-language:existentials",
    "-unchecked",
    "-Xfatal-warnings",
    "-explain-types",
    "-Ykind-projector",
    "-Xmax-inlines",
    "40"
  )
)

lazy val zio = "dev.zio" %% "zio" % "2.0.0"
lazy val zioLogging = "dev.zio" %% "zio-logging" % "2.0.1"
lazy val zioLoggingSlf4j = "dev.zio" %% "zio-logging-slf4j" % "2.0.1"
lazy val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.2.11"
lazy val logstashLogbackEncoder =
  "net.logstash.logback" % "logstash-logback-encoder" % "7.2"
lazy val zioHttp = "io.d11" %% "zhttp" % "2.0.0-RC10"

lazy val rootDependencies = Seq(
  zio,
  zioLogging,
  zioLoggingSlf4j,
  logbackClassic,
  logstashLogbackEncoder,
  zioHttp
)
