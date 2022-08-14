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
    (Model.initial, Cmd.None)

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
          Request.post(
            "http://localhost:12345/generate",
            Body.json(model.newProjectDetails.toJSON)
          ),
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

    case Msg.ProjectNameChange(name) =>
      (model.withName(name), Cmd.None)

    case Msg.WidthChange(width) =>
      (model.withWidth(width), Cmd.None)

    case Msg.HeightChange(height) =>
      (model.withHeight(height), Cmd.None)

  def view(model: Model): Html[Msg] =
    div(`class` := "container-fluid", style := "padding: 10; margin: 0;")(
      TitleBar.view ::
        NewProject.form
    )

  def subscriptions(model: Model): Sub[IO, Msg] =
    Sub.None
