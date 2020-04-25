package w2d

object Wireframe2DOM extends App {
  Examples.Basic1()

  def synthesize(rects: Seq[Rect], width: Int, height: Int): Container = {
    new WireframeVisualizer(rects, width, height).main(Array())
    null
  }
}