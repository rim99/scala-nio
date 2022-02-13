ThisBuild / organization := "io.apilet"
ThisBuild / scalaVersion := "3.1.0"
ThisBuild / version      := "0.1.0-SNAPSHOT"

val loggingDep = Seq(
  "com.outr" %% "scribe" % "3.6.10"
)

val testDep = Seq(
  "org.scalatest" %% "scalatest" % "3.2.10" % "test",
  "org.scalatestplus" %% "mockito-3-12" % "3.2.10.0" % "test"
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

lazy val apilet = project.in(file("."))
  .settings(
    name := "apilet",
  )
  .aggregate(
    `nio-core`,
    `nio-jvm`,
    `nio-example-mock-http`,
    `http`,
  )

lazy val `nio-core` = project.in(file("nio-core"))
  .settings(
    libraryDependencies ++= testDep
  )

lazy val `nio-jvm` = project.in(file("nio-jvm"))
  .dependsOn(`nio-core`)
  .settings(
    libraryDependencies ++= testDep
  )

lazy val `nio-example-mock-http` = project.in(file("nio-example-mock-http"))
  .dependsOn(`nio-core`)
  .dependsOn(`nio-jvm`)
  .settings(
    libraryDependencies ++= loggingDep
  )

lazy val `http` = project.in(file("http"))
  .dependsOn(`nio-core`)
  .settings(
    libraryDependencies ++= loggingDep ++ testDep
  )
