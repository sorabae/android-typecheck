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

package kr.ac.kaist.safe.analyzer.models.builtin

import kr.ac.kaist.safe.analyzer.domain.IClass
import kr.ac.kaist.safe.analyzer.models._

// TODO RegExp
object BuiltinRegExp extends FuncModel(
  name = "RegExp",
  // TODO @function
  code = EmptyCode(2),
  // TODO @construct
  construct = Some(EmptyCode()),
  protoModel = Some((BuiltinRegExpProto, F, F, F))
)

object BuiltinRegExpProto extends ObjModel(
  name = "RegExp.prototype",
  props = List(
    InternalProp(IClass, PrimModel("RegExp")),

    // TODO exec
    NormalProp("exec", FuncModel(
      name = "RegExp.prototype.exec",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO test
    NormalProp("test", FuncModel(
      name = "RegExp.prototype.test",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO toString
    NormalProp("toString", FuncModel(
      name = "RegExp.prototype.toString",
      code = EmptyCode(argLen = 0)
    ), T, F, T)
  )
)