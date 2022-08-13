import scala.sys.process._
import scala.language.postfixOps

import sbtwelcome._

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"

lazy val scala3Version = "3.1.3"

lazy val commonSettings: Seq[sbt.Def.Setting[_]] = Seq(
  version      := "0.0.1",
  scalaVersion := scala3Version,
  organization := "io.indigoengine",
  libraryDependencies ++= Seq(
    "org.scalameta" %%% "munit" % "0.7.29" % Test
  ),
  crossScalaVersions := Seq(scala3Version),
  scalafixOnCompile  := true,
  semanticdbEnabled  := true,
  semanticdbVersion  := scalafixSemanticdb.revision,
  autoAPIMappings    := true
)

lazy val editor =
  (project in file("editor"))
    .enablePlugins(ScalaJSPlugin)
    .settings(commonSettings: _*)
    .settings(
      name := "editor",
      libraryDependencies ++= Seq(
        "io.indigoengine" %%% "tyrian-io" % Dependancies.tyrianVersion,
        "io.indigoengine" %%% "tyrian-indigo-bridge" % Dependancies.tyrianVersion,
        "io.indigoengine" %%% "indigo"            % Dependancies.indigoVersion,
        "io.indigoengine" %%% "indigo-extras"     % Dependancies.indigoVersion,
        "io.indigoengine" %%% "indigo-json-circe" % Dependancies.indigoVersion
      ),
      scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) }
    )

lazy val server =
  project
    .settings(commonSettings: _*)
    .settings(
      name := "server",
      libraryDependencies ++= Seq(
        "org.http4s" %% "http4s-ember-server" % Dependancies.http4sVersion,
        "org.http4s" %% "http4s-ember-client" % Dependancies.http4sVersion,
        "org.http4s" %% "http4s-circe"        % Dependancies.http4sVersion,
        "org.http4s" %% "http4s-dsl"          % Dependancies.http4sVersion,
        "io.circe"   %% "circe-generic"       % Dependancies.circeVersion,
        "org.typelevel" %% "munit-cats-effect-3" % Dependancies.munitCatsEffectVersion % Test,
        "ch.qos.logback"   % "logback-classic" % Dependancies.logbackVersion,
        "io.indigoengine" %% "tyrian"          % Dependancies.tyrianVersion,
        "com.lihaoyi"     %% "os-lib"          % Dependancies.osLib
      )
    )

// This is just here for reference for now. Should remove later.
lazy val spa =
  project
    .enablePlugins(ScalaJSPlugin)
    .enablePlugins(ScalaJSBundlerPlugin)
    .settings(commonSettings: _*)
    .settings(
      name                            := "SPA",
      scalaJSUseMainModuleInitializer := true,
      scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
      libraryDependencies ++= Seq(
        "io.indigoengine" %%% "tyrian-io" % Dependancies.tyrianVersion
      ),
      // Source maps seem to be broken with bundler
      Compile / fastOptJS / scalaJSLinkerConfig ~= { _.withSourceMap(false) },
      Compile / fullOptJS / scalaJSLinkerConfig ~= { _.withSourceMap(false) }
    )

lazy val indigoEditorProject =
  (project in file("."))
    .enablePlugins(ScalaJSPlugin)
    .settings(commonSettings: _*)
    .settings(
      code := {
        val command = Seq("code", ".")
        val run = sys.props("os.name").toLowerCase match {
          case x if x contains "windows" => Seq("cmd", "/C") ++ command
          case _                         => command
        }
        run.!
      },
      name := "IndigoEditorProject"
    )
    .settings(
      logo := s"Indigo Editor (v${version.value})",
      usefulTasks := Seq(
        UsefulTask("b", "editor/fastLinkJS", "Build the editor frontend"),
        UsefulTask("start", "server/run", "Run the editor service"),
        UsefulTask("", "code", "Launch VSCode")
      ),
      logoColor        := scala.Console.MAGENTA,
      aliasColor       := scala.Console.BLUE,
      commandColor     := scala.Console.CYAN,
      descriptionColor := scala.Console.WHITE
    )
    .aggregate(editor, server, spa)

lazy val code =
  taskKey[Unit]("Launch VSCode in the current directory")
