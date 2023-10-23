name := """x"""
organization := "com.xerexchain"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.11"
semanticdbEnabled := true
semanticdbVersion := scalafixSemanticdb.revision
// scalacOptions += "-Ywarn-unused-import" // required by `RemoveUnused` rule

javaOptions in Test += "-Dconfig.file=conf/application.test.conf"

libraryDependencies ++= Seq(
  guice,
  // Included this dependency to resolve the exception: java.lang.ClassCastException: class org.slf4j.helpers.NOPLoggerFactory cannot be cast to class ch.qos.logback.classic.LoggerContext
  // higher versions cause exception: java.lang.NoSuchMethodError: 'void ch.qos.logback.classic.util.ContextInitializer.configureByResource(java.net.URL)'
  "ch.qos.logback" % "logback-classic" % "1.4.8",

  // To resolve the exception while testing: java.lang.NoSuchMethodException: org.fusesource.jansi.AnsiConsole.wrapSystemOut(java.io.PrintStream)
  // higher versions cause exception

  //
  "org.fusesource.jansi" % "jansi" % "1.18",
  "com.typesafe.slick" %% "slick" % "3.5.0-M4",
  "com.typesafe.slick" %% "slick-codegen" % "3.5.0-M4",
  "com.typesafe.play" %% "play-slick" % "5.2.0-M3",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.2.0-M3",
  "org.postgresql" % "postgresql" % "42.6.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
  "org.scalatest" %% "scalatest" % "3.2.16" % Test,
  "org.scalacheck" %% "scalacheck" % "1.17.0" % Test,
  "org.scalamock" %% "scalamock" % "5.1.0" % Test,
  "org.mockito" %% "mockito-scala" % "1.17.14" % Test,
  "com.h2database" % "h2" % "2.2.220" % Test,
  "org.flywaydb" %% "flyway-play" % "7.41.0",
  // This dependency causes exception while importing guice library
  // "net.codingwell" %% "scala-guice" % "7.0.0",
  "org.scodec" %% "scodec-core" % "1.11.9",
  "org.bouncycastle" % "bcprov-jdk15on" % "1.70",
  play.sbt.PlayImport.cacheApi,
  play.sbt.PlayImport.guice, // TODO Diff with line 1
  // play.sbt.PlayImport.ehcache,
  "com.github.karelcemus" %% "play-redis" % "2.7.0"
)
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.xerexchain.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.xerexchain.binders._"
