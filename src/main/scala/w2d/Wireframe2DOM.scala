package w2d
import scalafx.scene.paint.Color

object Wireframe2DOM extends App {
  //Examples.DOMVisualizerTest()
  val depthLimit:Int = 5;
  val white = Color.White
  synthesize(Examples.ComplexLayout(), 800, 1200)

  def rectLowerY(rect: Rect): Int = {rect.origin.y + rect.size.y}
  def rectLowerX(rect: Rect): Int = {rect.origin.x + rect.size.x}
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
  def sectionizeRow(rects : Seq[Rect]): Seq[Seq[Rect]] = sectionize(rects, rectLowerY, rectY)
  def sectionizeCol(rects : Seq[Rect]): Seq[Seq[Rect]] = sectionize(rects, rectLowerX, rectX)
  
  def sectionAnchor(sects : Seq[Seq[Rect]], sectsLower: Rect => Int, start: Int): Seq[Int] = {
    if (sects.length == 0) {
      Seq()
    } else {
      start +: (sects.init map {case sect => sect.map(rect => sectsLower(rect)).max})
    } 
  }
  def sectionAnchorRow(sects : Seq[Seq[Rect]], start: Int): Seq[Int] = sectionAnchor(sects, rectLowerY, start)
  def sectionAnchorCol(sects : Seq[Seq[Rect]], start: Int): Seq[Int] = sectionAnchor(sects, rectLowerX, start)

  // recursively synthesis a section horizationly or vertically
  // if row is true, the synthesis is conducted vertically
  def synthesizeHV(rects: Seq[Rect], width: Int, height: Int,x: Int = 0, y: Int = 0, depth: Int = 0, row: Boolean = true): Container = {
    if (rects.length == 0) {
      null
    } else if (depth > depthLimit || rects.length == 1) {
      null// plainDivs(rects, x, y)
    }
    else {
      val sectionizeHV = if (row) sectionizeRow _ else sectionizeCol _
      val sectionizeHVReverse = if (!row) sectionizeRow _ else sectionizeCol _
      val sectionAnchorHV = if (row) sectionAnchorRow _ else sectionAnchorCol _
      val anchorStart = if (row) y else x
      val hvdiv = if (row) vdiv _ else hdiv _

      val sections = sectionizeHV(rects)
      val sectionAnchors: Seq[Int] = sectionAnchorHV(sections, anchorStart)
      // We know that singleHDiv are simple, linear horizontal divs
      // complexStructue contains structure that needs futher synthesis
      val (singleHDiv, complexStructure) = ((0 until sections.length) zip sections) partition {
        case (index, section) => sectionizeHVReverse(section) forall {case newSec => newSec.length == 1}
      }
      // singleHDivWLen: (index, row, len of row)
      val singleHDivWLen: Seq[(Int, Seq[Rect], Int)] = singleHDiv map {
        case (index, section) => (index, section, section.length)}
      var childrenList: Seq[HVContainer] = Seq()

      // TODO identify grid, for other in singleHDiv, generate simple oneline structure
      // Note: Only identify grid when row = true

      // TODO for complexStructue, call synthesisHV with depth + 1 and row = !row
      val complexSections = complexStructure map {case (index, section) => {
        val sectionX = if (row) x else sectionAnchors(index)
        val sectionY = if (row) sectionAnchors(index) else y
        (index, synthesizeHV(section, width, height, sectionX, sectionY, depth+1, !row))
      }}

      hvdiv(width, height, white, false, 0, 0, 0, 0)(childrenList)
    }
  }

  def synthesize(rects: Seq[Rect], width: Int, height: Int): Container = {

    null
    //synthesizeByRow(rects, width, height)
  }


  // TODO generate divs

}