package w2d
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

import scalafx.scene.paint.Color

object Wireframe2DOM extends App {
  //Examples.DOMVisualizerTest()
  val depthLimit: Int = 5;
  val white = Color.White
<<<<<<< HEAD
  println(Examples.ComplexLayout())
  synthesize(Examples.ComplexLayout(), 800, 1200)
=======

  new DOMVisualizer(synthesize(Examples.ComplexLayout(), 800, 1200), 800, 1200).main(Array())
>>>>>>> 41b5078952f885ff464630424492c6d16f9efabc

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

  def sectionLower(sects : Seq[Seq[Rect]], sectsLower: Rect => Int): Seq[Int] = {
    if (sects.length == 0) {
      Seq()
    } else {
      sects map {case sect => sect.map(rect => sectsLower(rect)).max}
    } 
  }
  def sectionLowerRow(sects : Seq[Seq[Rect]]): Seq[Int] = sectionLower(sects, rectLowerY)
  def sectionLowerCol(sects : Seq[Seq[Rect]]): Seq[Int] = sectionLower(sects, rectLowerX)

  // recursively synthesis a section horizationly or vertically
  // if row is true, the synthesis is conducted vertically
  def synthesizeHV(rects: Seq[Rect], width: Int, height: Int,x: Int = 0, y: Int = 0, depth: Int = 0, row: Boolean = true): Container = {
    println(rects)
    if (rects.length == 0) {
      null
    } else if (depth > depthLimit) {
      // bruteforce partition the graph into hdivs and vdivs
      // dont search for grid
      null// plainDivs(rects, x, y)
    } else if (rects.length == 1) {
      val rect = rects.head
      div(rect.size.x, rect.size.y, top = rect.origin.y - y, left = rect.origin.x - x)
    }
    else {
      val sectionizeHV = if (row) sectionizeRow _ else sectionizeCol _
      val sectionizeHVReverse = if (!row) sectionizeRow _ else sectionizeCol _
      val sectionAnchorHV = if (row) sectionAnchorRow _ else sectionAnchorCol _
      val sectionLowerHV = if (row) sectionLowerRow _ else sectionLowerCol _
      val anchorStart = if (row) y else x
      val hvdiv = if (row) vdiv _ else hdiv _
      val hvdivReverse = if (row) hdiv _ else vdiv _

      val sections = sectionizeHV(rects)
      val sectionAnchors: Seq[Int] = sectionAnchorHV(sections, anchorStart)
      val sectionLowers: Seq[Int] = sectionLowerHV(sections)
      // We know that singleHVDiv are simple, linear horizontal divs
      // complexStructue contains structure that needs futher synthesis
      val (singleHVDiv, complexStructure) = ((0 until sections.length) zip sections) partition {
        case (index, section) => sectionizeHVReverse(section) forall {case newSec => newSec.length == 1}
      }
      // singleHVDivWLen: (index, row, len of row)
      val singleHVDivWLen: Seq[(Int, Seq[Rect], Int)] = singleHVDiv map {
        case (index, section) => (index, section, section.length)}
      var childrenList: Seq[Container] = Seq()
      // val (singleRectSection, multipleRectSection) = singleHVDiv partition {
      //   case (_, sect) => sect.length <= 1 
      // }

      // identify gridv
      // Note: Only identify grid when row = true?? This is not correct??
      val (possibleGridSect, complexStrip) = singleHVDiv partition {
        case (index, sect) => sect.forall(_.size == sect.head.size)
      }
      val possibleGridSectMap = possibleGridSect.toMap

      // (index, size, amount)
      val possibleGridCell: IndexedSeq[(Int, Vector2, Int, Seq[Rect])] = possibleGridSect map {
        case (index, sect) => (index, sect.head.size, sect.length, sect)
      }
  
      var gridList: Seq[(Int, Container)] = Seq()
      var addedToGrid = collection.mutable.Map(possibleGridSect map {case (index, _) => index -> false}: _*)
      var i = 0

      // end is inclusive
      // n is number of elements in one cell
      // TODO add common divisor test
      def conSeqSections(start: Int, end: Int): Seq[Seq[Rect]] = {
        (start to end) map { case i => possibleGridSectMap(i) }
      }
      def generateCell(rects: Seq[Rect], cellWidth: Int, cellHeight: Int, cellx: Int, celly: Int): Container = {
        val cellChildren = childrenHVContainer(rects, cellx, celly, !row)
        hvdiv(cellWidth, cellHeight, white, false, 0, 0, 0, 0)(cellChildren)
      }
      def generateGrid(start: Int, end: Int, n: Int): Container = {
        // TODO variable length element of cell need recursion
        val sectionX = if (row) x else sectionAnchors(start)
        val sectionY = if (row) sectionAnchors(start) else y
        val firstOrigin = possibleGridSectMap(start).head.origin
        val (gridX, gridY) = (firstOrigin.x, firstOrigin.y)
        val sectionWidth = if (row) width else sectionLowers(end) - sectionX
        val sectionHeight = if (row) sectionLowers(end) - sectionY else height
        var cellList = Seq[Container]()
        var processed = start
        var (gridWidth, gridHeight) = (0, 0)
        var (cellWidth, cellHeight) = (0, 0)
        if (possibleGridSectMap(start).length == 1 && n == 1) {
          val headEle = possibleGridSectMap(start).head
          cellWidth = headEle.size.x
          cellHeight = headEle.size.y
          val margin = if (row) {
            possibleGridSectMap(start + 1).head.origin.y - headEle.origin.y - cellHeight
          } else {
            possibleGridSectMap(start + 1).head.origin.x - headEle.origin.x - cellWidth
          }
          cellList = childrenSingleCell(conSeqSections(start, end) map {case rects =>rects.head}, gridX, gridY, !row, margin)
        } else {
          if (row) {
            cellWidth = if (possibleGridSectMap(start).length == 1) {
              possibleGridSectMap(start).head.size.x
            } else {
              possibleGridSectMap(start)(1).origin.x - possibleGridSectMap(start)(0).origin.x
            }
            cellHeight = possibleGridSectMap(start)(n).origin.y - possibleGridSectMap(start)(0).origin.y
          } else {
            cellWidth = possibleGridSectMap(start)(n).origin.x - possibleGridSectMap(start)(0).origin.x
            cellHeight = if (possibleGridSectMap(start).length == 1) {
              possibleGridSectMap(start).head.size.y
            } else {
              possibleGridSectMap(start)(1).origin.y - possibleGridSectMap(start)(0).origin.y
            }
          }
          while (processed < end) {
            (0 until possibleGridSectMap(processed).length) foreach {
              case i => {
                val (cellX, cellY) = (possibleGridSectMap(processed)(i).origin.x, possibleGridSectMap(processed)(i).origin.y)
                val rectsTemp = (processed until processed + n) map {case j => possibleGridSectMap(j)(i)}
                cellList = cellList :+ generateCell(rectsTemp, cellWidth, cellHeight, cellX, cellY)
              }
            }
            processed = processed + n
          }
        }
        grid(sectionWidth, sectionHeight, border = false)(cellList: _*)
      }

      var unsuccessfulGrid = Seq[(Int, Seq[Rect])]()
      while (i < possibleGridCell.length) {
        val (index, size, len, sect) = possibleGridCell(i)
        val later = possibleGridCell.drop(i + 1)
        var success = false
        if (!later.isEmpty) {
          // first search for same size in the future
          if (later exists {case (_, laterSize, _, _) => laterSize == size}) {
            def getRectSize(start: Int, end: Int): Seq[Vector2] = {
              var res = Seq[Vector2]()
              // TODO possible bug start == end
              if ((start until end) forall {case i => possibleGridSectMap.contains(i)}) {
                (start until end) foreach {case i => res = res :+ possibleGridSectMap(i).head.size}
              }
              res
            }
            def getGapSize(start: Int, end: Int): Seq[Int] = {
              var res = Seq[Int]()
              if ((start until end) forall {case i => possibleGridSectMap.contains(i)}) {
                if (start == end - 1) {
                  res = Seq(if (row) possibleGridSectMap(start).head.size.y else possibleGridSectMap(start).head.size.x)
                } else {
                  res = ((start until end).sliding(2) map {case Seq(x, y) => sectionLowers(y) - sectionLowers(x)}).toSeq
                }
              }
              res
            }
            val sameSize = later filter {case (_, laterSize, _, _) => laterSize == size}
            val dist = sameSize.head._1 - index
            val rectSize = getRectSize(index, sameSize.head._1)
            val gapSize = getGapSize(index, sameSize.head._1)
            var endSameSize = 1
            while (endSameSize <= sameSize.length) {
              val lastIndex = sameSize(endSameSize - 1)._1
              val thisIndex = if (endSameSize != sameSize.length) {
               sameSize(endSameSize)._1
              } else {
                sameSize.last._1 + dist
              }
              if (thisIndex - lastIndex == dist && 
                  getRectSize(lastIndex, thisIndex) == rectSize &&
                  getGapSize(lastIndex, thisIndex) == gapSize) {
                endSameSize = endSameSize + 1
              } else {
                endSameSize = sameSize.length + 1
              }
            }
            val validSize = endSameSize - 2
            if (validSize >= 0) {
              success = true
              val upToSize = sameSize(validSize)._1 + dist - 1
              // now try to form a grid
              (index to upToSize) foreach {case i => addedToGrid(i) = true}
              gridList = gridList :+ ((index, generateGrid(index, upToSize, dist)))
              // TODO add fail case
              i = upToSize + 1
            } 
          }
        }

        // TODO increase i
        if (!success) {
          addedToGrid(i) = true
          unsuccessfulGrid = unsuccessfulGrid :+ ((index), sect)
          i = i + 1
        }
      }
      
      val complexSections = (unsuccessfulGrid ++ complexStructure ++ complexStrip) map {case (index, section) => {
        val sectionX = if (row) x else sectionAnchors(index)
        val sectionY = if (row) sectionAnchors(index) else y
        val sectionWidth = if (row) width else sectionLowers(index) - sectionX
        val sectionHeight = if (row) sectionLowers(index) - sectionY else height
        (index, synthesizeHV(section, sectionWidth, sectionHeight, sectionX, sectionY, depth+1, !row))
      }}
      // TODO add the computed children to childrenlist
      childrenList = ((complexSections ++ gridList) sortWith {case ((index1, _), (index2, _)) => index1 < index2}) map {case (_, sect) => sect}

      val res = if(childrenList.length == 1) childrenList.head else hvdiv(width, height, white, false, 0, 0, 0, 0)(childrenList)
      println(res.toString(0))
      res
      
    }
  }

  // return the children to a hdiv if row else to a vdiv
  def childrenHVContainer(rects: Seq[Rect], x: Int, y: Int, row: Boolean): Seq[Container] = {
    val hvdiv = if (row) hdiv _ else vdiv _
    val sortedRects = rects sortBy {case rect => if (row) rectX(rect) else rectY(rect)}
    if (row) {
      var dx = x
      sortedRects map {case rect => {
        val res = div(rect.size.x, rect.size.y, top = rect.origin.y - y, left = rect.origin.x - dx)
        dx = rect.origin.x + rect.size.x
        res
      }} 
    } else {
      var dy = y
      sortedRects map {case rect => {
        val res = div(rect.size.x, rect.size.y, top = rect.origin.y - dy, left = rect.origin.x - x)
        dy = rect.origin.y + rect.size.y
        res
      }} 
    }
  }

  def childrenSingleCell(rects: Seq[Rect], x: Int, y: Int, row: Boolean, margin: Int): Seq[Container] = {
    val hvdiv = if (row) hdiv _ else vdiv _
    val sortedRects = rects sortBy {case rect => if (row) rectX(rect) else rectY(rect)}
    if (row) {
      sortedRects map {case rect => {
        div(rect.size.x, rect.size.y, right = margin)
      }} 
    } else {
      sortedRects map {case rect => {
        div(rect.size.x, rect.size.y, bottom = margin)
      }} 
    }
    
  }

  def synthesize(rects: Seq[Rect], width: Int, height: Int): Container = {
    val res = synthesizeHV(rects, width, height)
//    val convertedRectangles = DOMVisualizer.toRectangles(res, withBorderOnly = true)
//    print(s"Converted to ${convertedRectangles.length} rectangles:\n")
//    convertedRectangles.foreach { rectangle =>
//      print(s"Rect(Vector2(${rectangle.x.value.toInt}, ${rectangle.y.value.toInt}), Vector2(${rectangle.width.value.toInt}, ${rectangle.height.value.toInt})),\n")
//    }
    println("=== Result ===")
    println(res.toString(0))
    val html = "<html>\n<body>\n" + toHtml(res)(1) + "\n</body>\n</html>"
    println(html)
    Files.write(Paths.get("result.html"), html.getBytes(StandardCharsets.UTF_8))
    res
  }

  def toHtml(container: Container)(implicit tabs: Int = 0): String = {
    var style = ""
    style += s"width: ${container.size.x}px; "
    style += s"height: ${container.size.y}px; "
    style += s"margin: ${container.margin.top}px ${container.margin.right}px ${container.margin.bottom}px ${container.margin.left}px; "
    if (container.border) style += s"border: 2px solid black; "
    container.parent match {
      case hvContainer: HVContainer =>
        if (hvContainer.isHorizontal) {
          style += "display: inline-block; "
        }
      case gridContainer: GridContainer =>

      case _ =>
    }

    "\t".repeat(tabs) + "<div style=\"" + style + "\">\n" +
      container.children.map(it => toHtml(it)(tabs + 1)).mkString("\n") +
      "\t".repeat(tabs) + "</div>\n"
  }

  // TODO generate divs

}