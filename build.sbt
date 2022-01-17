val scala3Version = "3.1.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "nio4s",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version
  )

libraryDependencies ++= Seq(
  "com.novocode" % "junit-interface" % "0.11" % "test"
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
