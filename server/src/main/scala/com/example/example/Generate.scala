package com.example.example

import cats.effect.Async
import cats.syntax.all.*

object Generate:

  def gen[F[_]: Async](buildPath: os.Path): F[String] = 
    for {
      _ <- copyBlankProject(buildPath)
      _ <- writeSettings(buildPath)
    } yield "Done generating."
  
  def copyBlankProject[F[_]: Async](buildPath: os.Path): F[Unit] = Async[F].delay {
    val projectDir = buildPath

    println(">> Build dir is: " + projectDir)

    os.remove.all(projectDir)
    os.makeDir.all(projectDir)

    val from          = os.pwd / "newProjectTemplate"
    println(">> Copying from: " + from)

    val to            = projectDir
    println(">> Copying to  : " + to)

    val createFolders = true

    os.copy.over(from, to, createFolders)

    ()
  }

  def writeSettings[F[_]: Async](buildPath: os.Path): F[Unit] = Async[F].delay {
    val projectDir = buildPath

    val settings: String = 
      """title=Testing 123
      |width=800
      |height=600
      |""".stripMargin

    os.write(projectDir / "settings.properties", settings)

    ()
  }
