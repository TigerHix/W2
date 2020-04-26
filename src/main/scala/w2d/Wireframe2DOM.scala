package w2d

object Wireframe2DOM extends App {
  Examples.DOMVisualizerTest()

  def rectLowerY(rect: Rect): Int = {rect.origin.y + rect.size.y}
  def rectLowerX(rect: Rect): Int = {rect.origin.y + rect.size.y}

  def rectX(rect: Rect): Int = {rect.origin.x}
  def rectY(rect: Rect): Int = {rect.origin.y}

  def sectionize(rects: Seq[Rect], rectLowerXY: Rect => Int, rectXY: Rect => Int): Seq[Seq[Rect]] = {
    val rectSortedByEnd = rects.sortWith(rectLowerXY(_) < rectLowerXY(_))
    // sort the rects' lower y and obtain all distinct values
    val rectSorted = (rectSortedByEnd map {case rect => rectLowerXY(rect)}).toSet.toSeq.sortWith(_ < _)
    val rectRange = rectSortedByEnd map {case rect => rectXY(rect) until rectLowerXY(rect)}
    val clearRect = rectSorted filter {case row => (rectRange filter {case range => range.contains(row)}).length == 0}
    var addedUntil = 0
    val cut : Map[Int, Seq[Rect]] = (clearRect map {case row =>
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
    println(sectionizeCol(rects))
    println(sectionizeRow(rects))
    null
  }
}