package w2d

import scalafx.scene.paint.{Color, Paint}

case class Vector2(x: Int, y: Int) {
  def +(other: Vector2): Vector2 = Vector2(x + other.x, y + other.y)
  def -(other: Vector2): Vector2 = Vector2(x - other.x, y - other.y)
}

object Vector2 {
  val Zero: Vector2 = Vector2(0, 0)
}

case class Vector4(top: Int, right: Int, bottom: Int, left: Int)

object Vector4 {
  val Zero: Vector4 = Vector4(0, 0, 0, 0)
}


case class Rect(origin: Vector2, size: Vector2) {

}

class Container {
  var parent: Container = _
  var children: Seq[Container] = Seq()
  var size: Vector2 = Vector2.Zero
  var margin: Vector4 = Vector4.Zero
  var padding: Vector4 = Vector4.Zero
  var fill: Color = Color.White
  var border: Boolean = true

  // For DOMVisualizer use only
  var origin: Vector2 = Vector2.Zero
}

class HVContainer extends Container {
  var horizontal = true
  def isHorizontal: Boolean = horizontal
  def isVertical: Boolean = !isHorizontal
}

class GridContainer extends Container {
}