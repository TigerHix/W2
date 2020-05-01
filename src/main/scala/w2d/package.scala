import scalafx.scene.paint.Color

package object w2d {
  def vdiv(width: Int, height: Int, fill: Color = Color.White, border: Boolean = true, top: Int = 0, right: Int = 0, bottom: Int = 0, left: Int = 0)(children: Container*): HVContainer = {
    val container = new HVContainer
    container.horizontal = false
    container.fill = fill
    container.border = border
    container.size = Vector2(width, height)
    container.margin = Vector4(top, right, bottom, left)
    val nonNullChildren = children
    nonNullChildren foreach { it => it.parent = container }
    container.children = nonNullChildren
    container
  }

  def hdiv(width: Int, height: Int, fill: Color = Color.White, border: Boolean = true, top: Int = 0, right: Int = 0, bottom: Int = 0, left: Int = 0)(children: Container*): HVContainer = {
    val container = new HVContainer
    container.horizontal = true
    container.fill = fill
    container.border = border
    container.size = Vector2(width, height)
    container.margin = Vector4(top, right, bottom, left)
    val nonNullChildren = children
    nonNullChildren foreach { it => it.parent = container }
    container.children = nonNullChildren
    container
  }

  def grid(width: Int, height: Int, fill: Color = Color.White, border: Boolean = true, top: Int = 0, right: Int = 0, bottom: Int = 0, left: Int = 0, minWidth: Int = 0)(children: Container*): GridContainer = {
    val container = new GridContainer
    container.fill = fill
    container.border = border
    container.minWidth = minWidth
    container.size = Vector2(width, height)
    container.margin = Vector4(top, right, bottom, left)
    val nonNullChildren = children
    nonNullChildren foreach { it => it.parent = container }
    container.children = nonNullChildren
    container
  }

  def div(width: Int, height: Int, fill: Color = Color.White, border: Boolean = true, top: Int = 0, right: Int = 0, bottom: Int = 0, left: Int = 0): HVContainer = {
    val container = new HVContainer
    container.horizontal = false
    container.fill = fill
    container.border = border
    container.size = Vector2(width, height)
    container.margin = Vector4(top, right, bottom, left)
    container
  }
}