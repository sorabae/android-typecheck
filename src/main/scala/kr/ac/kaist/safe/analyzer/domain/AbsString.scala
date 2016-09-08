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

package kr.ac.kaist.safe.analyzer.domain

////////////////////////////////////////////////////////////////////////////////
// concrete string type
////////////////////////////////////////////////////////////////////////////////
case class Str(str: String) extends PValue
object Str {
  implicit def str2str(str: Str): String = str.str
}

////////////////////////////////////////////////////////////////////////////////
// string abstract domain
////////////////////////////////////////////////////////////////////////////////
trait AbsString extends AbsDomain[Str, AbsString] {
  def gamma: ConSet[Str]
  def gammaSingle: ConSingle[Str]
  def gammaSimple: ConSimple[Str]
  def gammaIsAllNums: ConSingle[Boolean]

  def ===(that: AbsString): AbsBool
  def <(that: AbsString): AbsBool

  def trim: AbsString
  def concat(that: AbsString): AbsString
  def charAt(pos: AbsNumber): AbsString
  def charCodeAt(pos: AbsNumber): AbsNumber
  def contains(that: AbsString): AbsBool
  def length: AbsNumber
  def toLowerCase: AbsString
  def toUpperCase: AbsString

  def isAllNums: Boolean
  def isAllNotNumbers: Boolean
  def isArrayIndex: AbsBool

  def toAbsNumber: AbsNumber
  def toAbsBoolean: AbsBool
}

trait AbsStringUtil extends AbsDomainUtil[Str, AbsString] {
  // abstraction from all number string
  val Number: AbsString

  // abstraction from all non-number string
  val NotNumber: AbsString
}