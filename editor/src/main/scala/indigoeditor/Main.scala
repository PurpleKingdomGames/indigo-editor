package indigoeditor

import cats.effect.IO
import indigoeditor.game.MyAwesomeGame
import tyrian.Html.*
import tyrian.*
import tyrian.cmds.Logger

import scala.scalajs.js.annotation.*

@JSExportTopLevel("TyrianApp")
object Main extends TyrianApp[Msg, Model]:

  val gameDivId1: String    = "my-game-1"
  val gameDivId2: String    = "my-game-2"
  val gameId1: IndigoGameId = IndigoGameId("reverse")
  val gameId2: IndigoGameId = IndigoGameId("combine")

  def init(flags: Map[String, String]): (Model, Cmd[IO, Msg]) =
    (Model.init, Cmd.Emit(Msg.StartIndigo))

  def update(model: Model): Msg => (Model, Cmd[IO, Msg]) =
    case Msg.Increment =>
      (model.increment, Cmd.None)

    case Msg.Decrement =>
      (model.decrement, Cmd.None)

    case Msg.NewContent(content) =>
      val cmds =
        Cmd.Batch(
          model.bridge.publish(gameId1, content),
          model.bridge.publish(gameId2, content)
        )
      (model.copy(field = content), cmds)

    case Msg.StartIndigo =>
      (
        model,
        Cmd.Batch(
          Cmd.SideEffect {
            MyAwesomeGame(model.bridge.subSystem(gameId1), true)
              .launch(
                gameDivId1,
                "width"  -> "200",
                "height" -> "200"
              )
          },
          Cmd.SideEffect {
            MyAwesomeGame(
              model.bridge.subSystem(gameId2),
              false
            )
              .launch(
                gameDivId2,
                "width"  -> "200",
                "height" -> "200"
              )
          }
        )
      )

    case Msg.IndigoReceive(msg) =>
      (model, Logger.consoleLog("(Tyrian) from indigo: " + msg))

  def view(model: Model): Html[Msg] =
    div(`class` := "container")(
      div(`class` := "row")(
        div(`class` := "col bodyText", id := gameDivId1)(),
        div(`class` := "col bodyText", id := gameDivId2)()
      ),
      div(`class` := "row")(
        div(`class` := "col bodyText")(
          p(b("Enter text below and check the browser console for output.")),
          p(
            "The text you add is sent to both running Indigo games, one send the message back reversed and the other sends it back doubled up with a '_' separator."
          ),
          input(
            placeholder := "Text to reverse",
            onInput(s => Msg.NewContent(s)),
            myStyle
          ),
          div(myStyle)(text(model.field.reverse))
        )
      ),
      div(`class` := "row")(
        div(`class` := "col bodyText", styles("text-align" -> "right"))(
          button(onClick(Msg.Decrement))(text("-"))
        ),
        div(`class` := "col bodyText", styles("text-align" -> "center"))(
          text(model.count.toString)
        ),
        div(`class` := "col bodyText", styles("text-align" -> "left"))(
          button(onClick(Msg.Increment))(text("+"))
        )
      )
    )

  def subscriptions(model: Model): Sub[IO, Msg] =
    Sub.Batch(
      model.bridge.subscribe { case msg =>
        Some(Msg.IndigoReceive(s"[Any game!] ${msg}"))
      },
      model.bridge.subscribe(gameId1) { case msg =>
        Some(Msg.IndigoReceive(s"[$gameDivId1] ${msg}"))
      },
      model.bridge.subscribe(gameId2) { case msg =>
        Some(Msg.IndigoReceive(s"[$gameDivId2] ${msg}"))
      }
    )

  private val myStyle =
    styles(
      CSS.width("100%"),
      CSS.height("40px"),
      CSS.padding("10px 0"),
      CSS.`font-size`("2em"),
      CSS.`text-align`("center")
    )

// opaque type Model = Int
// object Model:
//   def init: Model = 0

//   extension (i: Model)
//     def +(other: Int): Model = i + other
//     def -(other: Int): Model = i - other

final case class Model(
    count: Int,
    bridge: TyrianIndigoBridge[IO, String],
    field: String
):
  def increment: Model = this.copy(count = count + 1)
  def decrement: Model = this.copy(count = count - 1)

object Model:
  val init: Model =
    Model(0, TyrianIndigoBridge(), "")

enum Msg:
  case Increment
  case Decrement
  case NewContent(content: String)
  case StartIndigo
  case IndigoReceive(msg: String)
