package w2d

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.layout.{BorderPane, HBox, Pane, StackPane}
import scalafx.scene.shape.{Line, Rectangle}

class DOMVisualizer(rootContainer: Container, stageWidth: Int, stageHeight: Int) extends JFXApp {
  stage = new PrimaryStage {
    title = "DOM Visualizer"
    scene = new Scene {
      root = {
        val pane = new Pane() {
          children = DOMVisualizer.toRectangles(rootContainer).flatMap { rectangle =>
            if (rectangle.userData == "div") {
              val diagonal1 = Line(rectangle.x.value, rectangle.y.value, rectangle.x.value + rectangle.width.value, rectangle.y.value + rectangle.height.value)
              val diagonal2 = Line(rectangle.x.value + rectangle.width.value, rectangle.y.value, rectangle.x.value, rectangle.y.value + rectangle.height.value)
              Seq(rectangle, diagonal1, diagonal2)
            } else Seq(rectangle)
          } :+ {
            val root = Rectangle(0, 0, stageWidth, stageHeight)
            root.style = "-fx-fill: transparent; -fx-stroke: black; -fx-stroke-width: 1;"
            root
          }
        }
        val root = new HBox {
          padding = Insets(25, 25, 25, 25)
          children = List(pane)
        }
        root
      }
    }
    width = stageWidth * 1.25
    height = stageHeight * 1.25
  }
}

object DOMVisualizer {
  def toRectangles(container: Container, withBorderOnly: Boolean = false, depth: Int = 0): Seq[Rectangle] = {
    calculateAbsoluteOrigin(container)

    var trueWidth = container.size.x
    container match {
      case gridContainer: GridContainer =>
        trueWidth = gridContainer.minWidth
      case _ =>
    }

    val rectangle = Rectangle(container.origin.x, container.origin.y, trueWidth, container.size.y)

    rectangle.style = "-fx-stroke: " + {
      container match {
        case div: HVContainer if div.children.isEmpty => "black"
        case div: HVContainer if div.isHorizontal => "red"
        case div: HVContainer if !div.isHorizontal => "blue"
        case _: GridContainer => "green"
      }
    } + s"; -fx-stroke-width: ${math.max(0, 4 - depth)};"
    rectangle.fill = container.fill

    if (container.children.isEmpty) {
      rectangle.userData = "div"
    }

    val childrenRectangles = container.children flatMap { it => toRectangles(it, withBorderOnly, depth + 1) }

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
