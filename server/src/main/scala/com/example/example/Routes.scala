package com.example.example

import cats.effect.Async
import cats.implicits.*
import fs2.io.file.Files
import org.http4s.Header
import org.http4s.HttpRoutes
import org.http4s.MediaType
import org.http4s.StaticFile
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.`Content-Type`
import org.typelevel.ci._
import tyrian.Tyrian

import java.io.File

object Routes:

  val buildPath: os.Path = os.pwd / ".indigo-editor" / "indigo-editor-scratch"

  def routes[F[_]: Async: Files](ssr: SSR[F]): HttpRoutes[F] =
    val dsl = new Http4sDsl[F] {}
    import dsl.*

    HttpRoutes.of[F] {
      case GET -> Root =>
        Ok(
          Tyrian.render(true, HomePage.page),
          `Content-Type`(MediaType.text.html)
        )

      case request @ GET -> Root / "spa.js" =>
        val spa = fs2.io.file.Path(
          "."
        ) / "spa" / "target" / "scala-3.1.3" / "scalajs-bundler" / "main" / "spa-fastopt-bundle.js"
        StaticFile
          .fromPath(spa.absolute, Some(request))
          .getOrElseF(NotFound(spa.absolute.toString))

      case GET -> Root / "ssr" / in =>
        for {
          out  <- ssr.render(SSR.Input(in))
          resp <- Ok(out.toHtml, `Content-Type`(MediaType.text.html))
        } yield resp

      case GET -> Root / "ssr" =>
        for {
          out  <- ssr.render
          resp <- Ok(out.toHtml, `Content-Type`(MediaType.text.html))
        } yield resp

      case GET -> Root / "generate" =>
        for {
          t <- Async[F].realTime
          b <- Generate.gen(buildPath)
          r <- Ok(
            s"$b. (at: $t)",
            `Content-Type`(MediaType.text.plain),
            Header.Raw(CIString("Access-Control-Allow-Origin"), "*")
          )
        } yield r

      case GET -> Root / "run" =>
        for {
          t <- Async[F].realTime
          b <- Run.run(buildPath)
          r <- Ok(
            s"One day, this will run something. (at: $t)",
            `Content-Type`(MediaType.text.plain),
            Header.Raw(CIString("Access-Control-Allow-Origin"), "*")
          )
        } yield r
    }
