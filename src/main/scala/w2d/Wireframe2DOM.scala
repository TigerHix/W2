package w2d
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

import scalafx.scene.paint.Color

import scala.collection.immutable.ArraySeq

object Wireframe2DOM extends App {
  val depthLimit: Int = 5;
  val white = Color.White

  for (c <- 1 to 100) {
    println("==========")

    for ((container, i) <- Examples.examples.view.zipWithIndex) {
      val rectangles = Examples.toRect(container)
      println(s"Converted to ${rectangles.length} rectangles:")
      //println(rectangles)
      val (width, height) = (container.size.x, container.size.y)
      val synthesized: Container = time(s"Synthesize example $i") {
        synthesize(rectangles, width, height)
      }
      val html = time(s"Convert example $i to DOM") {
        toHtml(synthesized, width)
      }
      Files.write(Paths.get(s"example$i.html"), html.getBytes(StandardCharsets.UTF_8))
    }
  }

  /*var synthesized = synthesize(Examples.ComplexLayout(), 800, 1200)
  new DOMVisualizer(synthesized, 800, 1200)//.main(Array())
  val html = toHtml(synthesized, 800, 1200)
  println(html)
  Files.write(Paths.get("result.html"), html.getBytes(StandardCharsets.UTF_8))*/

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
    //println(rects)
    if (rects.length == 0) {
      null
    } else if (depth > depthLimit) {
      // bruteforce partition the graph into hdivs and vdivs
      // dont search for grid
      null// plainDivs(rects, x, y)
    } else if (rects.length == 1) {
      val rect = rects.head
      val childrenDiv = div(rect.size.x, rect.size.y)
      hdiv(rect.size.x, rect.size.y, top = rect.origin.y - y, left = rect.origin.x - x, border = false)(childrenDiv)
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
      def generateGrid(start: Int, end: Int, n: Int, x: Int, y: Int): Container = {
        // TODO variable length element of cell need recursion 
        val sectionX = if (row) x else sectionAnchors(start)
        val sectionY = if (row) sectionAnchors(start) else y
        val firstOrigin = possibleGridSectMap(start).head.origin
        val lastRect = possibleGridSectMap(start).last
        val (gridX, gridY) = (firstOrigin.x, firstOrigin.y)
        val (leftMargin, topMargin) = (gridX - x, gridY - y)
        var sectionWidth = if (row) lastRect.origin.x + lastRect.size.x - gridX  else sectionLowers(end) - sectionX
        var sectionHeight = if (row) sectionLowers(end) - sectionY else lastRect.origin.y + lastRect.size.y - gridY
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
            cellHeight = possibleGridSectMap(start+n)(0).origin.y - possibleGridSectMap(start)(0).origin.y
          } else {
            cellWidth = possibleGridSectMap(start+n)(0).origin.x - possibleGridSectMap(start)(0).origin.x
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
        val minWidth = if (row) lastRect.origin.x + lastRect.size.x - gridX else sectionLowers(end) - gridX
        if (row) {
          sectionWidth = cellWidth * possibleGridSectMap(start).length
          sectionHeight = (possibleGridSectMap(start+n)(0).origin.y - possibleGridSectMap(start)(0).origin.y) * (end + 1 - start) / n
        }
        else {
          sectionWidth = (possibleGridSectMap(start+n)(0).origin.x - possibleGridSectMap(start)(0).origin.x) * (end + 1 - start) / n
          sectionHeight = cellHeight * possibleGridSectMap(start).length
        }
        grid(sectionWidth, sectionHeight, border = false, top = topMargin, left = leftMargin, minWidth = minWidth)(cellList: _*)
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
              val gridX = if (row) x else sectionAnchors(index)
              val gridY = if (row) sectionAnchors(index) else y
              gridList = gridList :+ ((index, generateGrid(index, upToSize, dist, gridX, gridY)))
              // TODO add fail case
              i = i + upToSize - index + 1
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
    val container = synthesizeHV(rects, width, height)
    //print(container.toString(0))
    container
  }

  def toHtml(synthesized: Container, stageWidth: Int): String = {
    val style = s"max-width: ${stageWidth}px; margin: 0 auto; "
    "<html>\n<body style=\"" + style + "\">\n" + toDiv(synthesized)(stageWidth, 1) + "\n</body>\n</html>"
  }

  def toDiv(container: Container)(implicit stageWidth: Int, tabs: Int = 0): String = {
    var style = ""
    var aux = false
    var auxStyle = ""

    container match {
      case hvContainer: HVContainer =>
        style += "display: flex; flex-wrap: wrap; justify-content: center; align-items: flex-start;"
        if (hvContainer.children.isEmpty) style += s"height: ${container.size.y}px; "
        else style += s"height: auto; "
      case gridContainer: GridContainer =>
        style += s"height: auto;"
        aux = true
        auxStyle += "display: grid; grid-template-columns: repeat(auto-fit, " + gridContainer.children.head.size.x + "px); justify-content: center; grid-gap: " + gridContainer.children.head.margin.bottom +"px " + gridContainer.children.head.margin.right + "px;"
        // container.size = Vector2(gridContainer.minWidth, container.size.y)
        // Copy left margin to right margin TODO: Not
        // container.margin = Vector4(container.margin.top, container.margin.left, container.margin.bottom, container.margin.left)
      case _ =>
    }

    container.parent match {
      case hvContainer: HVContainer =>
        style += s"max-width: ${container.size.x}px; width: 100%; "
      case gridContainer: GridContainer =>
        if (container == gridContainer.children.last) {
          // Remove right margin
          //container.margin = Vector4(container.margin.top, 0, container.margin.bottom, container.margin.left)
        }
        style += s"width: ${container.size.x}px; "
        style += s"height: ${container.size.y}px; " // Override
      case _ =>
    }

    container match {
      case hvContainer: HVContainer =>
        if (hvContainer.children.size == 1) {
          style += s"margin: ${container.margin.top}px auto ${container.margin.bottom}px auto; "
        } else {
          style += s"margin: ${container.margin.top}px ${container.margin.right * 100.0 / stageWidth}% ${container.margin.bottom}px ${container.margin.left * 100.0 / stageWidth}%; "
        }
      case gridContainer: GridContainer =>
        style += s"margin: ${container.margin.top}px auto ${container.margin.bottom}px auto; "
    }
    // style += s"margin: ${container.margin.top}px ${container.margin.right}px ${container.margin.bottom}px ${container.margin.left}px; "
    if (container.border) style += s"outline: 2px solid " + {
      container match {
        case div: HVContainer if div.children.isEmpty => "black"
        case div: HVContainer if div.isHorizontal => "red"
        case div: HVContainer if !div.isHorizontal => "blue"
        case _: GridContainer => "green"
      }
    } + "; outline-offset: -2px;"

    "\t".repeat(tabs) + "<div class=\"" + { container.getClass.getSimpleName } + "\" style=\"" + style + "\">\n" + {
      if (aux)
        "\n" + "\t".repeat(tabs + 1) + "<div style=\"" + auxStyle + "\">\n"
      else
        ""
    } +
      container.children.map(it => toDiv(it)(stageWidth, tabs + (if (aux) 2 else 1))).mkString("\n") + {
      if (aux)
        "\n" + "\t".repeat(tabs + 1) + "</div>\n"
      else
        ""
    } +
    "\t".repeat(tabs) + "</div>\n"
  }

  /**
   * Credits: https://stackoverflow.com/a/9160068/2706176
   */
  def time[R](label: String)(block: => R): R = {
    val t0 = System.nanoTime()
    val result = block
    val t1 = System.nanoTime()
    val diff = t1 - t0
    println(s"$label: " + diff + "ns = " + (diff / 1000000) + "ms")
    result
  }

}