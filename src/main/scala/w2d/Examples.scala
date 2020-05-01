package w2d

object Examples {

  def toRect(container: Container): Seq[Rect] = {
    DOMVisualizer.toRectangles(container, withBorderOnly = true).map { rectangle =>
      Rect(Vector2(rectangle.x.value.toInt, rectangle.y.value.toInt), Vector2(rectangle.width.value.toInt, rectangle.height.value.toInt))
    }
  }

  val examples = List(
    // Example 1
    vdiv(800, 600, border = false)(
      div(800, 200),
      div(760, 190, top = 10, left = 20, right = 20),
      div(720, 190, top = 10, left = 40, right = 40)
    ),
    // Example 2
    hdiv(800, 600, border = false)(
      div(420, 580, top = 10, bottom = 10, left = 20),
      div(200, 580, top = 10, bottom = 10, left = 20),
      div(100, 580, top = 10, bottom = 10, left = 20, right = 20)
    ),
    // Example 3
    vdiv(800, 1000, border = false)(
      component1()
    ),
    // Example 4
    vdiv(800, 10000, border = false)(
      component1(),
      component1(),
      component1(),
      component1(),
      component1(),
      component1(),
      component1(),
      component1(),
      component1(),
      component1(),
    )
  )

  def component1(): Container = {
    vdiv(800, 1000, border = false)(
      hdiv(800, 50, border = false)(
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
  }
}
