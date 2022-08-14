package indigoeditor

import tyrian.Html
import tyrian.Html.*

object TitleBar:

  def view: Html[Msg] =
    div(id := "container")(
      img(src := "imgs/indigo_logo_solid_text.svg")
    )
    
