import scala.sys.process._
import scala.language.postfixOps

import sbtwelcome._

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"

lazy val scala3Version = "3.1.2"

lazy val commonSettings: Seq[sbt.Def.Setting[_]] = Seq(
  version      := Dependancies.tyrianVersion,
  scalaVersion := scala3Version,
  organization := "io.indigoengine",
  libraryDependencies ++= Seq(
    "org.scalameta" %%% "munit" % "0.7.29" % Test
  ),
  libraryDependencies ++= Seq(
    "io.indigoengine" %%% "tyrian-io" % Dependancies.tyrianVersion
  ),
  testFrameworks += new TestFramework("munit.Framework"),
  scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
  crossScalaVersions := Seq(scala3Version),
  scalafixOnCompile  := true,
  semanticdbEnabled  := true,
  semanticdbVersion  := scalafixSemanticdb.revision,
  autoAPIMappings    := true
)

lazy val bootstrap =
  (project in file("bootstrap"))
    .enablePlugins(ScalaJSPlugin)
    .settings(commonSettings: _*)
    .settings(name := "bootstrap")

lazy val electron =
  (project in file("electron"))
    .enablePlugins(ScalaJSPlugin)
    .settings(commonSettings: _*)
    .settings(name := "electron")

lazy val indigo =
  (project in file("indigo"))
    .enablePlugins(ScalaJSPlugin)
    .settings(commonSettings: _*)
    .settings(
      name := "indigo-bridge",
      libraryDependencies ++= Seq(
        "io.indigoengine" %%% "tyrian-indigo-bridge" % Dependancies.tyrianVersion,
        "io.indigoengine" %%% "indigo"            % Dependancies.indigoVersion,
        "io.indigoengine" %%% "indigo-extras"     % Dependancies.indigoVersion,
        "io.indigoengine" %%% "indigo-json-circe" % Dependancies.indigoVersion
      )
    )

lazy val exampleProjects: List[String] =
  List(
    "bootstrap",
    "electron",
    "indigo"
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
        UsefulTask("", "buildExamples", "Cleans and builds all examples"),
        UsefulTask("", "cleanAll", "Cleans all examples"),
        UsefulTask("", "compileAll", "Compiles all examples"),
        UsefulTask("", "testAll", "Tests all examples"),
        UsefulTask("", "fastOptAll", "Compiles all examples to JS"),
        UsefulTask("", "code", "Launch VSCode")
      ) ++ makeCmds(exampleProjects),
      logoColor        := scala.Console.MAGENTA,
      aliasColor       := scala.Console.BLUE,
      commandColor     := scala.Console.CYAN,
      descriptionColor := scala.Console.WHITE
    )

lazy val code =
  taskKey[Unit]("Launch VSCode in the current directory")

def makeCmds(names: List[String]): Seq[UsefulTask] =
  names.zipWithIndex.map { case (n, i) =>
    val cmd =
      List(
        s"$n/clean",
        s"$n/fastOptJS"
      ).mkString(";", ";", "")

    UsefulTask("build" + (i + 1), cmd, n)
  }.toSeq

// Top level commands
def applyCommand(projects: List[String], command: String): String =
  projects.map(p => p + "/" + command).mkString(";", ";", "")

def applyToAll(command: String): String =
  List(
    applyCommand(exampleProjects, command)
  ).mkString

addCommandAlias(
  "cleanAll",
  applyToAll("clean")
)
addCommandAlias(
  "compileAll",
  applyToAll("compile")
)
addCommandAlias(
  "testAll",
  applyToAll("test")
)
addCommandAlias(
  "fastOptAll",
  applyToAll("fastOptJS")
)
addCommandAlias(
  "buildExamples",
  List(
    "cleanAll",
    "compileAll",
    "fastOptAll"
  ).mkString(";", ";", "")
)
