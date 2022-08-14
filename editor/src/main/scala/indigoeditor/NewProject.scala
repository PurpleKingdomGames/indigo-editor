package indigoeditor

import tyrian.Html
import tyrian.Html.*
import tyrian.*

object NewProject:

  def form: List[Html[Msg]] =
    List(
      div(cls := "row")(
        div(cls := "col", style := "padding: 5px;")(
          div(cls := "input-group mb-3")(
            span(cls := "input-group-text")("Project name"),
            input(
              typ         := "text",
              cls         := "form-control",
              placeholder := "What shall we call your game?",
              onInput(v => Msg.ProjectNameChange(v))
            )
          )
        )
      ),
      div(cls := "row")(
        div(cls := "col", style := "padding: 5px;")(
          div(cls := "input-group mb-3")(
            span(cls := "input-group-text")("Window width"),
            input(
              typ         := "number",
              cls         := "form-control",
              min := 0,
              max := 4096,
              value := 550,
              onInput(v => Msg.WidthChange(v.toInt))
            )
          )
        )
      ),
      div(cls := "row")(
        div(cls := "col", style := "padding: 5px;")(
          div(cls := "input-group mb-3")(
            span(cls := "input-group-text")("Window height"),
            input(
              typ         := "number",
              cls         := "form-control",
              min := 0,
              max := 4096,
              value := 400,
              onInput(v => Msg.HeightChange(v.toInt))
            )
          )
        )
      ),
      div(cls := "row")(
        div(cls := "col", style := "padding: 5px;")(
          button(
            cls := "btn btn-primary",
            onClick(Msg.BuildAndRun)
          )("Build & Run")
        )
      )
    )
