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

import kr.ac.kaist.safe.LINE_SEP
import scala.collection.immutable.HashMap

// 10.2.1.2 Object Environment Records
class ObjEnvRecord(
    val loc: Loc
) extends EnvRecord {
  // TODO 10.2.1.2.1 HasBinding(N)
  def HasBinding(name: String): AbsBool = null

  // TODO 10.2.1.2.2 CreateMutableBinding(N, D)
  def CreateMutableBinding(name: String, del: Boolean): ObjEnvRecord = null

  // TODO 10.2.1.2.3 SetMutableBinding(N, V, S)
  def SetMutableBinding(
    name: String,
    v: AbsValue,
    strict: Boolean
  ): (ObjEnvRecord, Set[Exception]) = null

  // TODO 10.2.1.2.4 GetBindingValue(N, S)
  def GetBindingValue(
    name: String,
    strict: Boolean
  ): (AbsValue, Set[Exception]) = null

  // TODO 10.2.1.2.5 DeleteBinding(N)
  def DeleteBinding(name: String): (ObjEnvRecord, AbsBool) = null

  // TODO 10.2.1.2.6 ImplicitThisValue()
  def ImplicitThisValue: AbsValue = null
}