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

    case Msg.GenerateProject =>
      val decoder =
        Decoder[Msg](
          r => Msg.ServerResponse(r.status.code, r.body),
          e => Msg.ServerResponse(500, e.toString)
        )

      val request =
        Http.send[IO, Response, Msg](
          Request.get("http://localhost:12345/generate"),
          decoder
        )

      (model, request)

    case Msg.RunProject =>
      val decoder =
        Decoder[Msg](
          r => Msg.ServerResponse(r.status.code, r.body),
          e => Msg.ServerResponse(500, e.toString)
        )

      val request =
        Http.send[IO, Response, Msg](
          Request.get("http://localhost:12345/run"),
          decoder
        )

      (model, request)

    case Msg.ServerResponse(code, body) =>
      val msg: String =
        s"""Status: $code
        |Body:
        |$body
        """.stripMargin

      (model.copy(serverResponse = msg), Cmd.None)

  def view(model: Model): Html[Msg] =
    div(`class` := "container")(
      h1("Indigo Editor"),
      button(onClick(Msg.GenerateProject))("Generate"),
      button(onClick(Msg.RunProject))("Run"),
      p("server response:"),
      code(model.serverResponse)
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
  case GenerateProject
  case RunProject
  case ServerResponse(code: Int, body: String)
