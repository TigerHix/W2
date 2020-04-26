package w2d

object Wireframe2DOM extends App {
  Examples.DOMVisualizerTest()
  synthesize(Examples.ComplexLayout(), 800, 1200)

  def rectLowerY(rect: Rect): Int = {rect.origin.y + rect.size.y}
  def rectLowerX(rect: Rect): Int = {rect.origin.y + rect.size.y}

  def rectX(rect: Rect): Int = {rect.origin.x}
  def rectY(rect: Rect): Int = {rect.origin.y}

  def sectionize(rects: Seq[Rect], rectLowerXY: Rect => Int, rectXY: Rect => Int): Seq[Seq[Rect]] = {
    val rectSortedByEnd = rects.sortWith(rectLowerXY(_) < rectLowerXY(_))
    // sort the rects' lower y and obtain all distinct values
    val rectSorted = (rectSortedByEnd map (rect => rectLowerXY(rect))).distinct.sortWith(_ < _)
    val rectRange = rectSortedByEnd map (rect => rectXY(rect) until rectLowerXY(rect))
    val clearRect = rectSorted filter (row => (rectRange filter (range => range.contains(row))).length == 0)
    var addedUntil = 0
    val cut : Map[Int, Seq[Rect]] = (clearRect map { row =>
      row -> {
        var rectSeq = Seq[Rect]()
        var counter = addedUntil
        while (counter < rects.length && rectLowerXY(rectSortedByEnd(counter)) <= row) {
          rectSeq = rectSeq :+ rectSortedByEnd(counter)
          counter += 1
        }
        addedUntil = counter
        rectSeq
      }
    }).toMap

    cut.toSeq.sortBy(_._1) map {case (_, section) => section}
  }

  def sectionizeRow(rects : Seq[Rect]): Seq[Seq[Rect]] = {
    sectionize(rects, rectLowerY, rectY)
  }

  def sectionizeCol(rects : Seq[Rect]): Seq[Seq[Rect]] = {
    sectionize(rects, rectLowerX, rectX)
  }

  def synthesize(rects: Seq[Rect], width: Int, height: Int): Container = {
    //new WireframeVisualizer(rects, width, height).main(Array())
    print("Sectionize col\n")
    sectionizeCol(rects) foreach println
    print("Sectionize row\n")
    sectionizeRow(rects) foreach println
    null
  }

}