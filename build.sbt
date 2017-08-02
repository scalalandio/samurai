description in ThisBuild := "SAM-like syntax for scala 2.11"

scalaVersion in ThisBuild := "2.12.3"

crossScalaVersions in ThisBuild := Seq("2.11.11", "2.12.3")


lazy val root = project.in(file("."))
  .settings(
    publish := {},
    publishLocal := {}
  )
  .aggregate(samuraiJS, samuraiJVM)
  .dependsOn(samuraiJS, samuraiJVM)

lazy val samurai = crossProject
  .crossType(CrossType.Pure)
  .settings(
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
      libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.scalatest" %%% "scalatest" % "3.0.3" % "test"
    ),
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding", "UTF-8",
      "-feature",
      "-unchecked",
      "-Xfatal-warnings",
      "-Yno-adapted-args",
      //  "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard",
      "-Xfuture"
    )
  )

lazy val samuraiJVM = samurai.jvm
lazy val samuraiJS = samurai.js
