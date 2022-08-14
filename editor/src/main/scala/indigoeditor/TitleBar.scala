package indigoeditor

import tyrian.Html
import tyrian.Html.*

object TitleBar:

  def view: Html[Msg] =
    div(
      cls   := "row",
      style := "height: 50px; background-color: #29016A; m-0"
    )(
      div(
        cls   := "col",
        style := "height: inherit; padding: 5px; width: 40%;"
      )(
        img(
          style := "height: 100%;",
          src   := "imgs/indigo_logo_solid_text.svg",
          cls   := "img-fluid"
        )
      )
    )
