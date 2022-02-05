ThisBuild / organization := "io.apilet"
ThisBuild / scalaVersion := "3.1.0"
ThisBuild / version      := "0.1.0-SNAPSHOT"

lazy val nio4s = project.in(file("."))
  .settings(
    name := "nio4s",
  )
  .aggregate(
    `core`,
    `jvm`,
  )

val loggingDep = Seq(
  "com.outr" %% "scribe" % "3.6.10"
)

val testDep = Seq(
  "org.scalatest" %% "scalatest" % "3.2.10" % "test"
)

scalacOptions ++= {
  Seq(
    "-encoding",
    "UTF-8",
    "-feature",
    "-explain",
    "-unchecked",
    "-indent",
    "-rewrite",
    "-Ycheck-all-patmat",
    // disabled during the migration
    // "-Xfatal-warnings"
  )
}

lazy val core = project.in(file("core"))
  .settings(
    libraryDependencies ++= testDep
  )

lazy val jvm = project.in(file("jvm"))
  .dependsOn(core)
  .settings(
    libraryDependencies ++= testDep
  )
