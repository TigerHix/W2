package w2d

case class Vector2(x: Int, y: Int) {
  case object Zero extends Vector2(0, 0)
}

case class Vector4(top: Int, right: Int, bottom: Int, left: Int)

case class Rect(origin: Vector2, width: Int, height: Int) {

}

class Container(parent: Container, children: Seq[Container], origin: Vector2, width: Int, height: Int, margin: Vector4, isGrid: Boolean) {

}