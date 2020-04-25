package w2d

object Wireframe2DOM extends App {
  Examples.Basic1()

  def synthesize(rects: Seq[Rect], width: Int, height: Int): Container = {
    new WireframeVisualizer(rects, width, height).main(Array())
    val rectSortedByRow = rects.sortWith(_.origin.y < _.origin.y)
    val rowSet = (rectSortedByRow map {case rect => rect.origin.y + rect.size.y}).toSet
    val rectYRange = rectSortedByRow map {case rect => rect.origin.y to rect.origin.y + rect.size.y}
    // now we want to put rects that are potentially in the same container together
    
    


    null
  }
  
}