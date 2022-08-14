package com.example.example

import cats.effect.Async
import cats.implicits.*
import fs2.io.file.Files
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import org.http4s.Header
import org.http4s.Headers
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

      case OPTIONS -> Root / "generate" =>
        NoContent.headers(
          Headers.empty.put(
            Header.Raw(CIString("Access-Control-Allow-Origin"), "*"),
            Header.Raw(CIString("Allow"), "OPTIONS, POST"),
            Header.Raw(CIString("Accept"), "application/json"),
            Header.Raw(CIString("Access-Control-Allow-Headers"), "Content-Type")
          )
        )

      case req @ POST -> Root / "generate" =>
        val settings: F[Option[NewProject]] =
          req.bodyText.compile.string.map(s => decode[NewProject](s).toOption)

        for {
          t <- Async[F].realTime
          j <- settings
          b <- Generate.gen(buildPath, j)
          r <-
            j.map { s =>
              Ok(
                s"$b. (at: $t)",
                `Content-Type`(MediaType.text.plain),
                Header.Raw(CIString("Access-Control-Allow-Origin"), "*")
              )
            }.getOrElse {
              BadRequest("The data supplied was invalid")
            }
        } yield r

      case GET -> Root / "run" =>
        for {
          t <- Async[F].realTime
          b <- Run.run(buildPath)
          r <- Ok(
            s"Running. (at: $t)",
            `Content-Type`(MediaType.text.plain),
            Header.Raw(CIString("Access-Control-Allow-Origin"), "*")
          )
        } yield r
    }

final case class NewProject(name: String, width: Int, height: Int)
