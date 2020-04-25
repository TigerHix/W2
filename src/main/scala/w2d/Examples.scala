package w2d

object Examples {
  def Basic1(): Unit = {
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
}
