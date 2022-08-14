package indigoeditor

import cats.effect.IO
import tyrian.Html.*
import tyrian.*
import tyrian.cmds.Logger
import tyrian.http.*
import scala.scalajs.js.annotation.*

final case class Model(newProjectDetails: NewProjectDetails):
  def withName(newName: String): Model =
    this.copy(newProjectDetails = newProjectDetails.withName(newName))

  def withWidth(newWidth: Int): Model =
    this.copy(newProjectDetails = newProjectDetails.withWidth(newWidth))

  def withHeight(newHeight: Int): Model =
    this.copy(newProjectDetails = newProjectDetails.withHeight(newHeight))

object Model:
  val initial: Model =
    Model(NewProjectDetails.initial)

final case class NewProjectDetails(
    name: String,
    width: Int,
    height: Int
):
  def withName(newName: String): NewProjectDetails =
    this.copy(name = newName)

  def withWidth(newWidth: Int): NewProjectDetails =
    this.copy(width = newWidth)

  def withHeight(newHeight: Int): NewProjectDetails =
    this.copy(height = newHeight)

  def toJSON: String = {
    import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
    this.asJson.noSpaces
  }

object NewProjectDetails:
  val initial: NewProjectDetails =
    NewProjectDetails("Indigo Game", 550, 400)
