package indigoeditor

import cats.effect.IO
import tyrian.Html.*
import tyrian.*
import tyrian.cmds.Logger
import tyrian.http.*

import scala.scalajs.js.annotation.*

enum Msg:
  case NoOp
  case Log(message: String)
  case BuildAndRun
  case ServerResponse(code: Int, body: String)
  case ProjectNameChange(name: String)
  case WidthChange(width: Int)
  case HeightChange(height: Int)
