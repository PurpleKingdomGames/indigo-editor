package indigoeditor

import cats.effect.IO
import tyrian.Html.*
import tyrian.*
import tyrian.cmds.Logger
import tyrian.http.*

import scala.scalajs.js.annotation.*

@JSExportTopLevel("TyrianApp")
object Main extends TyrianApp[Msg, Model]:

  def init(flags: Map[String, String]): (Model, Cmd[IO, Msg]) =
    (Model.init, Cmd.None)

  def update(model: Model): Msg => (Model, Cmd[IO, Msg]) =
    case Msg.NoOp =>
      (model, Cmd.None)

    case Msg.Log(msg) =>
      (model, Logger.info(msg))

    case Msg.BuildAndRun =>
      val decoder =
        Decoder[Msg](
          r => Msg.ServerResponse(r.status.code, r.body),
          e => Msg.ServerResponse(500, e.toString)
        )

      val build =
        Http.send[IO, Response, Msg](
          Request.get("http://localhost:12345/generate"),
          decoder
        )

      val run =
        Http.send[IO, Response, Msg](
          Request.get("http://localhost:12345/run"),
          decoder
        )

      (model, build |+| run)

    case Msg.ServerResponse(code, body) =>
      val msg: String =
        s"""Status: $code
        |Body:
        |$body
        """.stripMargin

      (model, Logger.info(msg))

  def view(model: Model): Html[Msg] =
    div(`class` := "container")(
      TitleBar.view,
      h1("Indigo Editor"),
      button(onClick(Msg.BuildAndRun))("Build & Run")
    )

  def subscriptions(model: Model): Sub[IO, Msg] =
    Sub.None

final case class Model(serverResponse: String)

object Model:
  val init: Model =
    Model("---")

enum Msg:
  case NoOp
  case Log(message: String)
  case BuildAndRun
  case ServerResponse(code: Int, body: String)
