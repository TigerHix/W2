package w2d

object Wireframe2DOM extends App {
  Examples.Basic1()

  def synthesize(rects: Seq[Rect], width: Int, height: Int): Container = {
    def rectLowerY(rect: Rect):Int = {
      rect.origin.y + rect.size.y
    }
    //new WireframeVisualizer(rects, width, height).main(Array())
    val rectSortedByRow = rects.sortWith(rectLowerY(_) < rectLowerY(_))
    val rectNumber = rects.length
    // sort the rects' lower y and obtain all distinct values
    val rowSorted = (rectSortedByRow map {case rect => rectLowerY(rect)}).toSet.toSeq.sortWith(_ < _)
    val rectYRange = rectSortedByRow map {case rect => rect.origin.y until rectLowerY(rect)}
    val clearRow = rowSorted filter {case row => (rectYRange filter {case range => range.contains(row)}).length == 0}
    var addedUntil = 0
    val firstCutByRow : Map[Int, Seq[Rect]] = (clearRow map {case row => 
      row -> {
        var rectSeq = Seq[Rect]()
        var counter = addedUntil
        while (counter < rectNumber && rectLowerY(rectSortedByRow(counter)) <= row) {
          rectSeq = rectSeq :+ rectSortedByRow(counter)
          counter += 1
        }
        addedUntil = counter
        rectSeq
      }
    }).toMap
    println(firstCutByRow)

    null
  }
  
}