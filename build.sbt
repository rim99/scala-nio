ThisBuild / organization := "io.rim99"
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

val testDep = Seq(
  "com.github.sbt" % "junit-interface" %  "0.13.2" % Test
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
