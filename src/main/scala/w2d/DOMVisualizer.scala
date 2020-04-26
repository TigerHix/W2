package w2d

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.layout.Pane
import scalafx.scene.shape.{Line, Rectangle}

class DOMVisualizer(rootContainer: Container, stageWidth: Int, stageHeight: Int) extends JFXApp {
  stage = new PrimaryStage {
    title = "DOM Visualizer"
    scene = new Scene {
      root = new Pane {
        padding = Insets(0)
        children = DOMVisualizer.toRectangles(rootContainer, withBorderOnly = true).flatMap { rectangle =>
          val diagonal1 = Line(rectangle.x.value, rectangle.y.value, rectangle.x.value + rectangle.width.value, rectangle.y.value + rectangle.height.value)
          val diagonal2 = Line(rectangle.x.value + rectangle.width.value, rectangle.y.value, rectangle.x.value, rectangle.y.value + rectangle.height.value)
          Seq(rectangle, diagonal1, diagonal2)
        } :+ {
          val root = Rectangle(0, 0, stageWidth, stageHeight)
          root.style = "-fx-fill: transparent; -fx-stroke: red; -fx-stroke-width: 1;"
          root
        }
    }
    }
    width = stageWidth * 1.25
    height = stageHeight * 1.25
  }
}

object DOMVisualizer {
  def toRectangles(container: Container, withBorderOnly: Boolean = false): Seq[Rectangle] = {
    calculateAbsoluteOrigin(container)

    val rectangle = Rectangle(container.origin.x, container.origin.y, container.size.x, container.size.y)
    if (container.border) rectangle.style = "-fx-stroke: black; -fx-stroke-width: 1;"
    else rectangle.style = "-fx-stroke-width: 0;"
    rectangle.fill = container.fill

    val childrenRectangles = container.children flatMap { it => toRectangles(it, withBorderOnly) }

    if (!withBorderOnly || (withBorderOnly && container.border)) rectangle +: childrenRectangles
    else childrenRectangles
  }

  def calculateAbsoluteOrigin(parent: Container): Unit = {
    parent match {
      case h: HVContainer if h.isHorizontal =>
        var dx = 0
        parent.children.foreach { child =>
          child.origin = Vector2(parent.origin.x + child.margin.left + dx, parent.origin.y + child.margin.top)
          dx += child.margin.left + child.size.x + child.margin.right
        }
      case v: HVContainer if v.isVertical =>
        var dy = 0
        parent.children.foreach { child =>
          child.origin = Vector2(parent.origin.x + child.margin.left, parent.origin.y + child.margin.top + dy)
          dy += child.margin.top + child.size.y + child.margin.bottom
        }
      case grid: GridContainer =>
        var dx = 0
        var dy = 0
        var rowHeight = 0
        parent.children.foreach { child =>
          if (dx + child.margin.left + child.size.x + child.margin.right > grid.size.x) {
            // New row
            dy += rowHeight
            dx = 0
            rowHeight = 0
          }

          child.origin = Vector2(parent.origin.x + child.margin.left + dx, parent.origin.y + child.margin.top + dy)
          dx += child.margin.left + child.size.x + child.margin.right
          rowHeight = math.max(rowHeight, child.margin.top + child.size.y + child.margin.bottom)
        }
    }

    parent.children foreach calculateAbsoluteOrigin
  }
}
