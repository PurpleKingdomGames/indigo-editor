package indigoeditor

import cats.effect.IO
import tyrian.Html.*
import tyrian.*
import tyrian.cmds.Logger

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

  def view(model: Model): Html[Msg] =
    div(`class` := "container")(
      p("Hello")
    )

  def subscriptions(model: Model): Sub[IO, Msg] =
    Sub.None

final case class Model()

object Model:
  val init: Model =
    Model()

enum Msg:
  case NoOp
  case Log(message: String)
