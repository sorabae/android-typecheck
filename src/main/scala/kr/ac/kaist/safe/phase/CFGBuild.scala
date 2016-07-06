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

import scala.util.{ Try, Success }
import kr.ac.kaist.safe.{ LINE_SEP, SafeConfig }
import kr.ac.kaist.safe.cfg_builder.{ DefaultCFGBuilder, DotWriter, AddrGen }
import kr.ac.kaist.safe.nodes.ir.IRRoot
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.util._

// CFGBuild phase
case object CFGBuild extends PhaseObj[IRRoot, CFGBuildConfig, CFG] {
  val name: String = "cfgBuilder"
  val help: String =
    "Builds a control flow graph for JavaScript source files." + LINE_SEP +
      "The files are concatenated in the given order before being parsed."
  def apply(
    ir: IRRoot,
    safeConfig: SafeConfig,
    config: CFGBuildConfig
  ): Try[CFG] = {
    // Build CFG from IR.
    val cbResult = new DefaultCFGBuilder(ir, safeConfig, config)
    val cfg = cbResult.cfg
    val excLog = cbResult.excLog

    // Report errors.
    if (excLog.hasError) {
      println(cfg.relFileName + ":")
      println(excLog)
    }

    // Pretty print to file.
    config.outFile.map(out => {
      val (fw, writer) = Useful.fileNameToWriters(out)
      writer.write(cfg.toString(0))
      writer.close
      fw.close
      println("Dumped CFG to " + out)
    })

    // print dot file: {dotName}.gv, {dotName}.pdf
    config.dotName.map(name => {
      DotWriter.spawnDot(cfg, None, None, None, s"$name.gv", s"$name.pdf")
    })

    Success(cfg)
  }

  def defaultConfig: CFGBuildConfig = CFGBuildConfig()
  val options: List[PhaseOption[CFGBuildConfig]] = List(
    ("verbose", BoolOption(c => c.verbose = true),
      "messages during compilation are printed."),
    ("out", StrOption((c, s) => c.outFile = Some(s)),
      "the resulting CFG will be written to the outfile."),
    ("dot", StrOption((c, s) => c.dotName = Some(s)),
      "the resulting CFG will be drawn to the {name}.gv and {name}.pdf")
  )
}

// CFGBuild phase config
case class CFGBuildConfig(
  var verbose: Boolean = false,
  var outFile: Option[String] = None,
  var dotName: Option[String] = None,
  var addrGen: AddrGen = new AddrGen
// TODO add option for cfg builder
) extends Config
