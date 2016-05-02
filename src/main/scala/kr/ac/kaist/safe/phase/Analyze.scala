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

package kr.ac.kaist.safe.phase

import kr.ac.kaist.safe.config.{ BoolOption, Config, ConfigOption, NumOption, OptionKind, StrOption }
import kr.ac.kaist.safe.nodes.CFG
import kr.ac.kaist.safe.analyzer.domain.{ AbsUndefUtil, AbsNullUtil, AbsBoolUtil, AbsNumberUtil, AbsStringUtil, DUndefUtil, DNullUtil, DBoolUtil, DNumUtil, DStrSetUtil }

// Analyze phase struct.
case class Analyze(
    prev: CFGBuild = CFGBuild(),
    analyzeConfig: AnalyzeConfig = AnalyzeConfig()
) extends Phase(Some(prev), Some(analyzeConfig)) {
  override def apply(config: Config): Unit = analyze(config)
  def analyze(config: Config): Unit = {}
  def analyze(config: Config, cfg: CFG): Unit = {
    //TODO: DefaultAnalyzer.analyze(config, analyzeConfig, ...)
  }
}

// Analyze phase helper.
object Analyze extends PhaseHelper {
  def create: Analyze = Analyze()
}

// Config options for Analyze phase.
case class AnalyzeConfig(
    var verbose: Boolean = false,
    var outFile: Option[String] = None,
    var AbsUndef: AbsUndefUtil = DUndefUtil,
    var AbsNull: AbsNullUtil = DNullUtil,
    var AbsBool: AbsBoolUtil = DBoolUtil,
    var AbsNumber: AbsNumberUtil = DNumUtil,
    var AbsString: AbsStringUtil = new DStrSetUtil(0)
) extends ConfigOption {
  val prefix: String = "analyze:"
  val optMap: Map[String, OptionKind] = Map(
    "verbose" -> BoolOption(() => verbose = true),
    "out" -> StrOption((s: String) => outFile = Some(s)),
    "maxStrSetSize" -> NumOption((n: Int) => if (n > 0) AbsString = new DStrSetUtil(n))
  )
}