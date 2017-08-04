description in ThisBuild := "SAM-like syntax for scala 2.11"

scalaVersion in ThisBuild := "2.12.3"

crossScalaVersions in ThisBuild := Seq("2.11.11", "2.12.3")


lazy val root = project.in(file("."))
  .settings(noPublishSettings : _*)
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
  .settings(publishSettings : _*)

lazy val samuraiJVM = samurai.jvm
lazy val samuraiJS = samurai.js

lazy val publishSettings = Seq(
  organization := "io.scalaland",
  homepage := Some(url("https://scalaland.io")),
  licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  scmInfo := Some(
    ScmInfo(url("https://github.com/scalalandio/samurai"), "scm:git:git@github.com:scalalandio/samurai.git")
  ),
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ =>
    false
  },
  pomExtra := (
    <developers>
      <developer>
        <id>krzemin</id>
        <name>Piotr Krzemiński</name>
        <url>http://github.com/krzemin</url>
      </developer>
    </developers>
    )
)

lazy val noPublishSettings =
  Seq(publish := (), publishLocal := (), publishArtifact := false)
