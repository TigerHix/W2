package w2d

import scalafx.scene.paint.Color

object Examples {
  def Basic(): Unit = {
    Wireframe2DOM.synthesize(
      Seq(
        Rect(Vector2(20, 20), Vector2(760, 120)),
        Rect(Vector2(20, 160), Vector2(184, 184)),
        Rect(Vector2(20 + 184 + 8, 160), Vector2(184, 184)),
        Rect(Vector2(20 + 184 + 8 + 184 + 8, 160), Vector2(184, 184)),
        Rect(Vector2(20 + 184 + 8 + 184 + 8 + 184 + 8, 160), Vector2(184, 184)),
        Rect(Vector2(20, 370), Vector2(760, 200)),
      ),
      800,
      600
    )
  }

  def WireframeVisualizerTest(): Unit = {
    new WireframeVisualizer(Seq(
      Rect(Vector2(20, 10), Vector2(160, 30)),
      Rect(Vector2(220, 10), Vector2(260, 30)),
      Rect(Vector2(520, 10), Vector2(260, 30)),
      Rect(Vector2(0, 50), Vector2(800, 140)),
      Rect(Vector2(200, 220), Vector2(400, 20)),
      Rect(Vector2(105, 265), Vector2(140, 145)),
      Rect(Vector2(105, 415), Vector2(140, 20)),
      Rect(Vector2(255, 265), Vector2(140, 145)),
      Rect(Vector2(255, 415), Vector2(140, 20)),
      Rect(Vector2(405, 265), Vector2(140, 145)),
      Rect(Vector2(405, 415), Vector2(140, 20)),
      Rect(Vector2(555, 265), Vector2(140, 145)),
      Rect(Vector2(555, 415), Vector2(140, 20)),
      Rect(Vector2(105, 445), Vector2(140, 145)),
      Rect(Vector2(105, 595), Vector2(140, 20)),
      Rect(Vector2(255, 445), Vector2(140, 145)),
      Rect(Vector2(255, 595), Vector2(140, 20)),
      Rect(Vector2(10, 650), Vector2(780, 30)),
      Rect(Vector2(50, 700), Vector2(160, 160)),
      Rect(Vector2(230, 700), Vector2(160, 160)),
      Rect(Vector2(410, 700), Vector2(160, 160)),
      Rect(Vector2(590, 700), Vector2(160, 160)),
      Rect(Vector2(30, 880), Vector2(440, 100)),
      Rect(Vector2(490, 890), Vector2(80, 80)),
      Rect(Vector2(590, 890), Vector2(80, 80)),
      Rect(Vector2(690, 890), Vector2(80, 80)),
    ), 800, 1000).main(Array())
  }

  def DOMVisualizerTest(): Unit = {
    val container =
      vdiv(800, 1000, border = false)(
        hdiv(800, 50, Color.AliceBlue, border = false)(
          div(160, 30, left = 20, right = 20, top = 10),
          div(260, 30, left = 20, right = 20, top = 10),
          div(260, 30, left = 20, right = 20, top = 10)
        ),
        div(800, 140),
        div(400, 20, left = 200, top = 30),
        vdiv(800, 400, border = false)(
          grid(600, 400, left = 100, border = false, top = 20)(
            vdiv(150, 180, border = false)(
              div(140, 145, top = 5, left = 5, bottom = 5),
              div(140, 20, left = 5, bottom = 5)
            ),
            vdiv(150, 180, border = false)(
              div(140, 145, top = 5, left = 5, bottom = 5),
              div(140, 20, left = 5, bottom = 5)
            ),
            vdiv(150, 180, border = false)(
              div(140, 145, top = 5, left = 5, bottom = 5),
              div(140, 20, left = 5, bottom = 5)
            ),
            vdiv(150, 180, border = false)(
              div(140, 145, top = 5, left = 5, bottom = 5),
              div(140, 20, left = 5, bottom = 5)
            ),
            vdiv(150, 180, border = false)(
              div(140, 145, top = 5, left = 5, bottom = 5),
              div(140, 20, left = 5, bottom = 5)
            ),
            vdiv(150, 180, border = false)(
              div(140, 145, top = 5, left = 5, bottom = 5),
              div(140, 20, left = 5, bottom = 5)
            ),
          )
        ),
        div(780, 30, left = 10, right = 10, top = 10),
        vdiv(800, 200, border = false)(
          grid(720, 180, top = 10, left = 40, border = false)(
            div(160, 160, top = 10, bottom = 10, left = 10, right = 10),
            div(160, 160, top = 10, bottom = 10, left = 10, right = 10),
            div(160, 160, top = 10, bottom = 10, left = 10, right = 10),
            div(160, 160, top = 10, bottom = 10, left = 10, right = 10)
          )
        ),
        hdiv(800, 100, border = false)(
          div(440, 100, left = 30, right = 10),
          hdiv(300, 100, border = false)(
            div(80, 80, top = 10, bottom = 10, left = 10, right = 10),
            div(80, 80, top = 10, bottom = 10, left = 10, right = 10),
            div(80, 80, top = 10, bottom = 10, left = 10, right = 10)
          )
        )
      )

    val convertedRectangles = DOMVisualizer.toRectangles(container, withBorderOnly = true)
    print(s"Converted to ${convertedRectangles.length} rectangles:\n")
    convertedRectangles.foreach { rectangle =>
      print(s"Rect(Vector2(${rectangle.x.value.toInt}, ${rectangle.y.value.toInt}), Vector2(${rectangle.width.value.toInt}, ${rectangle.height.value.toInt})),\n")
    }

    new DOMVisualizer(container, 800, 1000).main(Array())
  }
}
