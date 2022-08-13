package com.example.example

import cats.effect.Async

object Run:

  def run[F[_]: Async](buildPath: os.Path): F[String] = Async[F].delay {

    os.proc("mill", "mygame.runGame")
        .call(cwd = buildPath, stdin = os.Inherit, stdout = os.Inherit, stderr = os.Inherit)

    "Done running."
  }

