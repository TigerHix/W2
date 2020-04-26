package w2d

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.layout.Pane
import scalafx.scene.shape.Rectangle

class WireframeVisualizer(rects: Seq[Rect], stageWidth: Int, stageHeight: Int) extends JFXApp {
  stage = new PrimaryStage {
    title = "Wireframe Visualizer"
    scene = new Scene {
      root = new Pane {
        padding = Insets(0)
        children = {
          val root = Rectangle(0, 0, stageWidth, stageHeight)
          root.style = "-fx-fill: white; -fx-stroke: red; -fx-stroke-width: 1;"
          root
        } +: rects.map { rect =>
          val rectangle = Rectangle(rect.origin.x, rect.origin.y, rect.size.x, rect.size.y)
          rectangle.style = "-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 1;"
          rectangle
        }
      }
    }
    width = stageWidth * 1.25
    height = stageHeight * 1.25
  }
}
