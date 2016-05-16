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

package kr.ac.kaist.safe.ast_rewriter

import kr.ac.kaist.safe.errors.ExcLog
import kr.ac.kaist.safe.errors.error._
import kr.ac.kaist.safe.nodes._
import kr.ac.kaist.safe.util.{ NodeUtil => NU, Span }
import kr.ac.kaist.safe.config.Config

/**
 * Eliminates ambiguities in an AST that can be resolved solely by knowing what
 * kind of entity a name refers to.  This class specifically handles
 * the following:
 *  - Variable/function/label references have unique internal names.
 *  - All name references that are undefined or used incorrectly are
 *    treated as static errors.
 */
class Disambiguator(program: Program) extends ASTWalker {
  /* Error handling
   * The signal function collects errors during the disambiguation phase.
   * To collect multiple errors,
   * we should return a dummy value after signaling an error.
   */
  var excLog: ExcLog = new ExcLog
  val assExcLog: ExcLog = new ExcLog

  /* Environment for renaming identifiers. */
  type Env = List[(String, String)]
  val emptyLabel = ("empty", "empty")
  var env: Env = Config.PRED_VARS.map(v => (v, v)) ++
    Config.PRED_FUNS.map(f => (f, f)) ++ List(
      ("alert", "alert"),
      (NU.internalPrint, NU.internalPrint)
    )
  // encIter  : the label set of an enclosing IterationStatement
  // encSwitch: the label set of an enclosing SwitchStatement
  // encStmt  : the label set of an enclosing statement
  // curStmt  : the label set of the current statement
  class LabEnv(val encIter: Env, val encSwitch: Env, val encStmt: Env, val curStmt: Env)
  val emptyLabEnv = new LabEnv(List(), List(), List(), List())
  var labEnv: LabEnv = emptyLabEnv
  val thisName = "this"
  val argName = "arguments"

  /* 15.1.4 Constructor Properties of the Global Object
   * Object, Function, Array, String, Boolean, Number, Date, RegExp, Error,
   * EvalError, RangeError, ReferenceError, SyntaxError, TypeError, URIError
   */
  def addEnv(name: String, newid: Id): Unit = newid.uniqueName match {
    case None => excLog.signal(IdNotBoundError(name, newid))
    case Some(uniq) => env = (name, uniq) :: env
  }
  def addEnv(id: Id, newid: Id): Unit = newid.uniqueName match {
    case None => excLog.signal(IdNotBoundError(id.text, id))
    case Some(uniq) => env = (id.text, uniq) :: env
  }
  def addEnv(newid: Id): Unit = newid.uniqueName match {
    case None => excLog.signal(IdNotBoundError(newid.text, newid))
    case Some(uniq) => env = (newid.text, uniq) :: env
  }
  def addLEnv(id: Id, newid: Id): Unit = newid.uniqueName match {
    case None => excLog.signal(IdNotBoundError(id.text, id))
    case Some(uniq) =>
      labEnv = new LabEnv(labEnv.encIter, labEnv.encSwitch, labEnv.curStmt ++ labEnv.encStmt, (id.text, uniq) :: labEnv.curStmt)
  }
  def setLEnvCur(lab: (String, String)): Unit =
    labEnv = new LabEnv(labEnv.encIter, labEnv.encSwitch, labEnv.curStmt ++ labEnv.encStmt, List(lab))
  def setLEnv(n: ASTNode): LabEnv = {
    val oldLEnv = labEnv
    labEnv = new LabEnv(labEnv.encIter, labEnv.encSwitch, labEnv.curStmt ++ labEnv.encStmt, List())
    oldLEnv
  }
  def resetLEnv(lenv: LabEnv): Unit = labEnv = lenv

  def getEnvNoCheck(id: Id): String = {
    val name = id.text
    env.find { case (n, _) => n.equals(name) } match {
      case Some((_, uniq)) => uniq
      case None =>
        val newName = newId(id).uniqueName.get
        env = (name, newName) :: env
        newName
    }
  }

  def getEnvCheck(id: Id): String = {
    val name = id.text
    env.find { case (n, _) => n.equals(name) } match {
      case Some((_, uniq)) => uniq
      case None =>
        if (!inWith)
          assExcLog.signal(IdNotBoundError(id.text, id))
        name
    }
  }

  def inEnv(id: Id): Boolean = {
    val name = id.text
    env.find { case (n, _) => n.equals(name) } match {
      case Some(_) => true
      case None => false
    }
  }
  def setEnv(envs: (Env, LabEnv)): Unit = envs match {
    case (e, le) => env = e; labEnv = le
  }

  def newId(span: Span, n: String): Id =
    Id(NU.makeASTNodeInfo(span), n, Some(n), false)
  def newId(id: Id): Id = id match {
    case Id(info, text, _, _) =>
      if (toplevel) Id(info, text, Some(text), false)
      else Id(info, text, Some(NU.freshName(text)), false)
  }
  def newId(id: Id, uniq: String): Id = id match {
    case Id(info, text, _, _) => Id(info, text, Some(uniq), false)
  }
  def newLabel(label: Label): Label = label match {
    case Label(info, id) => Label(info, newId(id))
  }
  def newLabel(label: Label, uniq: String): Label = label match {
    case Label(info, id) => Label(info, newId(id, uniq))
  }
  def newPropId(id: Id): PropId = id match {
    case Id(info, text, _, _) =>
      PropId(info, Id(info, text, Some(text), false))
  }

  def checkDuplicatedProperty(members: List[Member]): Unit = {
    var member1Str: String = ""
    var member2Str: String = ""
    for (member1 <- members) {
      member1Str = NU.member2Str(member1)
      for (member2 <- members if member1.ne(member2)) {
        member2Str = NU.member2Str(member2)
        if (member1Str.equals(member2Str)) (member1, member2) match {
          case (Field(_, _, _), GetProp(_, _, _)) =>
            excLog.signal(DataAccPropError(member1Str, member2))
          case (Field(_, _, _), SetProp(_, _, _)) =>
            excLog.signal(DataAccPropError(member1Str, member2))
          case (GetProp(_, _, _), Field(_, _, _)) =>
            excLog.signal(DataAccPropError(member1Str, member2))
          case (SetProp(_, _, _), Field(_, _, _)) =>
            excLog.signal(DataAccPropError(member1Str, member2))
          case (GetProp(_, _, _), GetProp(_, _, _)) =>
            excLog.signal(GetPropError(member1Str, member2))
          case (SetProp(_, _, _), SetProp(_, _, _)) =>
            excLog.signal(SetPropError(member1Str, member2))
          case _ =>
        }
      }
    }
  }

  /* The main entry function */
  def doit(): Program = {
    val result = NU.simplifyWalker.walk(walk(program))
    if (!hasAssign) excLog += assExcLog
    result
  }

  var toplevel = false
  /* IterationStatement: DoWhile, While, For, ForIn */
  var inIterator = false
  var inSwitch = false
  var inFunctionBody = false
  var inWith = false
  var hasAssign = false
  def isAssignOp(op: Op): Boolean = op match {
    case Op(_, text) => text.equals("++") || text.equals("--")
  }
  def mkInIterator(): Unit = {
    inIterator = true
    if (labEnv.curStmt.isEmpty) setLEnvCur(emptyLabel)
    labEnv = new LabEnv(labEnv.curStmt ++ labEnv.encIter, labEnv.encSwitch, labEnv.encStmt, labEnv.curStmt)
  }
  def mkInSwitch(): Unit = {
    inSwitch = true
    if (labEnv.curStmt.isEmpty) setLEnvCur(emptyLabel)
    labEnv = new LabEnv(labEnv.encIter, labEnv.curStmt ++ labEnv.encSwitch, labEnv.encStmt, labEnv.curStmt)
  }
  def functional(i: ASTNodeInfo, span: Span, name: Id, params: List[Id], fds: List[FunDecl],
    vds: List[VarDecl], body: SourceElements, bodyS: String): Functional = {
    val oldToplevel = toplevel
    toplevel = false
    labEnv = emptyLabEnv
    addEnv(argName, newId(newId(span, argName)))
    val pairsParams = params.map(p => (p, newId(p)))
    pairsParams.foreach { case (p, nid) => addEnv(p, nid) }
    fds.foreach(fd => addEnv(fd.ftn.name, newId(fd.ftn.name)))
    val newVds = vds.foldLeft(List[VarDecl]())((vds, vd) => vd match {
      case VarDecl(info, id, _, strict) => params.find(p => p.text.equals(id.text)) match {
        case None =>
          val nid = newId(id)
          addEnv(id, nid)
          vds :+ VarDecl(info, nid, None, strict)
        case _ => vds
      }
    })
    val newFds = fds.map(walk)
    val oldInFunctionBody = inFunctionBody
    inFunctionBody = true
    val newBody = body match {
      case SourceElements(i, stmts, strict) =>
        SourceElements(i, stmts.map(walk), strict)
    }
    inFunctionBody = oldInFunctionBody
    toplevel = oldToplevel
    Functional(i, newFds, newVds, newBody, name, pairsParams.map { case (_, nid) => nid }, bodyS)
  }

  override def walk(node: Program): Program = node match {
    case Program(info, TopLevel(it, fds, vds, body)) =>
      fds.foreach(fd => addEnv(fd.ftn.name, newId(fd.info.span, fd.ftn.name.text)))
      val newVds = vds.map(p => p match {
        case VarDecl(i, id, _, strict) =>
          val nid = newId(i.span, id.text)
          addEnv(id, nid)
          VarDecl(info, nid, None, strict)
      })
      val newFds = fds.map(walk)
      toplevel = true
      Program(info, TopLevel(it, newFds, newVds, body.map(walk)))
  }

  override def walk(node: SourceElements): SourceElements = node match {
    case SourceElements(info, stmts, strict) =>
      SourceElements(info, stmts.map(walk), strict)
  }

  override def walk(node: FunDecl): FunDecl = node match {
    case FunDecl(info, Functional(i, fds, vds, body, name, params, bodyS), strict) =>
      val oldEnv = (env, labEnv)
      val newName = newId(name, getEnvNoCheck(name))
      val result = FunDecl(
        info,
        functional(i, info.span, newName, params, fds, vds, body, bodyS), strict
      )
      setEnv(oldEnv)
      result
  }

  override def walk(node: LHS): LHS = node match {
    case FunExpr(info, Functional(i, fds, vds, body, name, params, bodyS)) =>
      val oldEnv = (env, labEnv)
      val oldToplevel = toplevel
      toplevel = false
      val newName = newId(name)
      addEnv(name, newName)
      val result = FunExpr(
        info,
        functional(i, info.span, newName, params, fds, vds, body, bodyS)
      )
      setEnv(oldEnv)
      toplevel = oldToplevel
      result
    case VarRef(info, id) => VarRef(info, newId(id, getEnvCheck(id)))
    case ObjectExpr(info, members) =>
      checkDuplicatedProperty(members)
      val oldLEnv = setLEnv(node)
      val result = super.walk(node)
      resetLEnv(oldLEnv)
      result
    case RegularExpression(info, body, flags) => {
      val regexp = "RegExp"
      New(info, FunApp(info, VarRef(info, Id(info, regexp, Some(regexp), false)),
        List(
          StringLiteral(info, "\"", body, true),
          StringLiteral(info, "\"", flags, false)
        )))
    }
    case _ =>
      val oldLEnv = setLEnv(node)
      val result = super.walk(node)
      resetLEnv(oldLEnv)
      result
  }

  override def walk(node: Member): Member = node match {
    case GetProp(info, prop, Functional(i, fds, vds, body, name, params, bodyS)) =>
      val oldEnv = (env, labEnv)
      val newProp = newPropId(name)
      val newName = newProp.id
      val result = GetProp(info, newProp,
        functional(i, info.span, newName, params, fds, vds, body, bodyS))
      setEnv(oldEnv)
      result
    case SetProp(info, prop, Functional(i, fds, vds, body, name, params, bodyS)) =>
      val oldEnv = (env, labEnv)
      val newProp = newPropId(name)
      val newName = newProp.id
      val result = SetProp(info, newProp,
        functional(i, info.span, newName, params, fds, vds, body, bodyS))
      setEnv(oldEnv)
      result
    case _ =>
      val oldLEnv = setLEnv(node)
      val result = super.walk(node)
      resetLEnv(oldLEnv)
      result
  }

  override def walk(node: Catch): Catch = node match {
    case Catch(info, id, body) =>
      val oldToplevel = toplevel
      toplevel = false
      val oldEnv = (env, labEnv)
      val nid = newId(id)
      addEnv(id, nid)
      setLEnv(node)
      val result = Catch(info, nid, body.map(walk))
      setEnv(oldEnv)
      toplevel = oldToplevel
      result
  }

  override def walk(node: Stmt): Stmt = node match {
    /* 12.8 The break Statement
     * A program is considered syntactically incorrect if either of the following is true:
     *  - The program contains a break statement without the optional Identifier,
     *    which is not nested, directly or indirectly (but not crossing function boundaries),
     *    within an IterationStatement or a SwitchStatement.
     *  - The program contains a break statement with the optional Identifier,
     *    where Identifier does not appear in the label set of an enclosing
     *    (but not crossing function boundaries) Statement.
     */
    case br @ Break(info, target) =>
      val newTarget = target match {
        case Some(label) => labEnv.encStmt.find { case (n, _) => n.equals(label.id.text) } match {
          case None => labEnv.encSwitch.find { case (n, _) => n.equals(label.id.text) } match {
            case None =>
              excLog.signal(OutsideBreakError(br, "a label"))
              return EmptyStmt(info)
            case Some((_, uniq)) => Some(newLabel(label, uniq))
          }
          case Some((_, uniq)) => Some(newLabel(label, uniq))
        }
        case None =>
          if (!inIterator && !inSwitch) {
            excLog.signal(OutsideBreakError(br, "an iterator or a switch."))
            return EmptyStmt(info)
          } else None
      }
      Break(info, newTarget)

    /* 12.7 The continue Statement
     * A program is considered syntactically incorrect if either of the following is true:
     *  - The program contains a continue statement without the optional Identifier,
     *    which is not nested, directly or indirectly (but not crossing function boundaries),
     *    within an IterationStatement.
     *  - The program contains a continue statement with the optional Identifier,
     *    where Identifier does not appear in the label set of an enclosing
     *    (but not crossing function boundaries) IterationStatement.
     */
    case c @ Continue(info, target) =>
      if (!inIterator) {
        excLog.signal(OutsideContError(c, "an iterator."))
        return EmptyStmt(info)
      } else {
        val newTarget = target match {
          case Some(label) => labEnv.encIter.find { case (n, _) => n.equals(label.id.text) } match {
            case None =>
              excLog.signal(OutsideContError(c, "a label."))
              return EmptyStmt(info)
            case Some((_, uniq)) => Some(newLabel(label, uniq))
          }
          case None => None
        }
        Continue(info, newTarget)
      }

    case _: DoWhile =>
      val oldEnv = (env, labEnv)
      val oldInIterator = inIterator
      mkInIterator
      val result = super.walk(node)
      inIterator = oldInIterator
      setEnv(oldEnv)
      result

    case _: For =>
      val oldEnv = (env, labEnv)
      val oldInIterator = inIterator
      mkInIterator
      val result = super.walk(node)
      inIterator = oldInIterator
      val newEnv = env
      setEnv(oldEnv)
      env = newEnv
      result

    case _: ForIn =>
      val oldEnv = (env, labEnv)
      hasAssign = true
      val oldInIterator = inIterator
      mkInIterator
      val result = super.walk(node)
      inIterator = oldInIterator
      val newEnv = env
      setEnv(oldEnv)
      env = newEnv
      result

    case fv: ForVar =>
      excLog.signal(NotReplacedByHoisterError(fv))
      fv

    case fv: ForVarIn =>
      excLog.signal(NotReplacedByHoisterError(fv))
      fv

    case ls @ LabelStmt(info, label @ Label(_, Id(_, name, _, _)), stmt) =>
      labEnv.curStmt.find { case (n, _) => n.equals(name) } match {
        case Some(_) =>
          excLog.signal(MultipleLabelDeclError(name, ls))
          ls
        case None =>
          val oldEnv = (env, labEnv)
          val nlabel = newLabel(label)
          addLEnv(label.id, nlabel.id)
          val result = LabelStmt(info, nlabel, walk(stmt))
          setEnv(oldEnv)
          result
      }

    /* 12.9 The return Statement
     * An ECMAScript program is considered syntactically incorrect if it contains
     * a return statement that is not within a FunctionBody.
     */
    case rt @ Return(info, expr) =>
      if (!inFunctionBody)
        excLog.signal(OutsideRetrunError(rt))
      val oldLEnv = setLEnv(node)
      val result = super.walk(rt)
      resetLEnv(oldLEnv)
      result

    case _: Switch =>
      val oldEnv = (env, labEnv)
      val oldInSwitch = inSwitch
      mkInSwitch
      val result = super.walk(node)
      inSwitch = oldInSwitch
      setEnv(oldEnv)
      result

    case vs: VarStmt =>
      excLog.signal(NotReplacedByHoisterError(vs))
      vs

    case _: While =>
      val oldEnv = (env, labEnv)
      val oldInIterator = inIterator
      mkInIterator
      val result = super.walk(node)
      inIterator = oldInIterator
      setEnv(oldEnv)
      result

    case _: With =>
      val oldEnv = (env, labEnv)
      val oldInWith = inWith
      inWith = true
      setLEnv(node)
      val result = super.walk(node)
      inWith = oldInWith
      setEnv(oldEnv)
      result

    case _ =>
      val oldLEnv = setLEnv(node)
      val result = super.walk(node)
      resetLEnv(oldLEnv)
      result
  }

  override def walk(node: Expr): Expr = node match {
    case AssignOpApp(info, Parenthesized(_, lhs), op, right) if lhs.isInstanceOf[LHS] =>
      val oldLEnv = setLEnv(node)
      val result = walk(AssignOpApp(info, lhs.asInstanceOf[LHS], op, right))
      resetLEnv(oldLEnv)
      result

    case AssignOpApp(info, VarRef(_, id), op, right) =>
      hasAssign = true
      val oldLEnv = setLEnv(node)
      val oldToplevel = toplevel
      val nid = if (!inEnv(id)) {
        toplevel = true
        newId(id)
      } else newId(id, getEnvNoCheck(id))
      toplevel = oldToplevel
      val result = AssignOpApp(info, VarRef(info, nid), op, walk(right))
      resetLEnv(oldLEnv)
      if (!inEnv(id)) {
        toplevel = true
        addEnv(id, newId(id))
        toplevel = oldToplevel
      }
      result

    case _: AssignOpApp =>
      hasAssign = true
      val oldLEnv = setLEnv(node)
      val result = super.walk(node)
      resetLEnv(oldLEnv)
      result

    case _: UnaryAssignOpApp =>
      hasAssign = true
      val oldLEnv = setLEnv(node)
      val result = super.walk(node)
      resetLEnv(oldLEnv)
      result

    case n: PrefixOpApp =>
      if (isAssignOp(n.op)) hasAssign = true
      val oldLEnv = setLEnv(node)
      val result = super.walk(node)
      resetLEnv(oldLEnv)
      result

    case _ =>
      val oldLEnv = setLEnv(node)
      val result = super.walk(node)
      resetLEnv(oldLEnv)
      result
  }
}