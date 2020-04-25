package w2d

case class Vector2(x: Int, y: Int)

object Vector2 {
  val Zero: Vector2 = Vector2(0, 0)
}

case class Vector4(top: Int, right: Int, bottom: Int, left: Int)

case class Rect(origin: Vector2, size: Vector2) {

}

class Container(parent: Container, children: Seq[Container], origin: Vector2, size: Vector2, padding: Vector4)

class GridContainer(parent: Container, children: Seq[Container], origin: Vector2, size: Vector2, padding: Vector4)
  extends Container(parent, children, origin, size, padding) {

}