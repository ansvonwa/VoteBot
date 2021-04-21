val dottyVersion = "3.0.0-RC2"
val scala2Version = "2.13.5"

Global / onChangedBuildSource := ReloadOnSourceChanges

enablePlugins(ScalaNativePlugin)
addCompilerPlugin("org.scala-native" % "junit-plugin" % "0.4.0" cross CrossVersion.full)

lazy val root = project
  .in(file("."))
  .settings(
    name := "votebot",
    version := "0.1.6",

    scalaVersion := scala2Version,

    nativeLinkStubs := true,

    libraryDependencies += "org.scala-native" %%% "junit-runtime" % "0.4.0" //% "test",
//    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",
  )
