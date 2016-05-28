/**
 * *****************************************************************************
 * Copyright (c) 2016, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.util

import java.io.File
import java.io.IOException
import java.io.Serializable
import java.lang.StringBuilder
import xtc.tree.Location
import kr.ac.kaist.safe.nodes.Node

case class Span(
    fileName: String = "defaultSpan",
    begin: SourceLoc = SourceLoc(),
    end: SourceLoc = SourceLoc()
) extends UIDObject {
  def addLines(line: Int, offset: Int): Span =
    Span(fileName, begin.addLines(line, offset), end.addLines(line, offset))

  def fileNameOnly: String = {
    val index = fileName.lastIndexOf(File.separatorChar)
    fileName.substring(index + 1)
  }

  override def toString: String =
    appendToStr(new StringBuilder).toString

  def toStringWithoutFiles: String =
    appendToStr(new StringBuilder, false).toString

  private def appendToStr(w: StringBuilder, doFiles: Boolean = true): String = {
    if (doFiles) {
      // TODO Need to add escapes to the file name
      w.append(fileName)
      w.append(":")
    }
    w.append(begin.toString)
    begin.line == end.line match {
      case true => begin.column == end.column match {
        case true =>
        case false => w.append("-").append(end.column)
      }
      case false => w.append("-").append(end.toString)
    }
    w.toString
  }

  // constructor
  def this(
    fileName: String,
    startLine: Int,
    endLine: Int,
    startC: Int,
    endC: Int,
    startOffset: Int,
    endOffset: Int
  ) = this(
    fileName,
    SourceLoc(startLine, startC, startOffset),
    SourceLoc(endLine, endC, endOffset)
  )

  def +(o: Span): Span = fileName == o.fileName match {
    case true => Span(fileName, begin, o.end)
    case false => Span(NodeUtil.MERGED_FILE_NAME)
  }

  // TODO is it really need?
  // val fileName = Useful.windowPathToUnixPath(f)
  // /**
  //  * Span which includes both the given spans.  Assumption: they're
  //  * from the same file.  If this is not true, the results will be
  //  * unpredictable.
  //  */
  // def span(a: Span, b: Span): Unit = {
  //   if (beginsEarlierThan(a, b))
  //     begin = a.begin
  //   else
  //     begin = b.begin
  //   if (endsLaterThan(a, b))
  //     end = a.end
  //   else
  //     end = b.end
  // }
  // def convertNameSeparatorToSlash(fileName: String): String =
  //   if (File.separatorChar == '/') fileName
  //   else fileName.replace(File.separatorChar, '/')
}

object Span {
  def merge(left: Span, right: Span): Span = left + right

  def merge(nodes: List[Node], defaultSpan: Span): Span = nodes match {
    case Nil => defaultSpan
    case first :: _ => first.span + nodes.last.span
  }
}
