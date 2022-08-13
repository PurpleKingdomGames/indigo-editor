package com.example.example

import cats.effect.Async

object Generate:

  def gen[F[_]: Async]: F[String] = Async[F].delay {
    val projectDir = os.pwd / ".indigo-editor" / "indigo-editor-scratch"

    println(">> Build dir is: " + projectDir)

    os.remove.all(projectDir)
    os.makeDir.all(projectDir)

    val from          = os.pwd / "newProjectTemplate"
    println(">> Copying from: " + from)

    val to            = projectDir
    println(">> Copying to  : " + to)

    val createFolders = true

    os.copy.over(from, to, createFolders)

    "All done"
  }
