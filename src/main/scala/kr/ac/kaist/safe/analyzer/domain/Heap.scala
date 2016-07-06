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

import scala.collection.immutable.HashMap
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.analyzer.models.PredefLoc.{ GLOBAL, SINGLE_PURE_LOCAL }

trait Heap {
  val map: Map[Loc, Obj]

  /* partial order */
  def <=(that: Heap): Boolean
  /* join */
  def +(that: Heap): Heap
  /* meet */
  def <>(that: Heap): Heap
  /* lookup */
  def apply(loc: Loc): Option[Obj]
  def getOrElse(loc: Loc, default: Obj): Obj
  /* heap update */
  def update(loc: Loc, obj: Obj): Heap
  /* remove location */
  def remove(loc: Loc): Heap
  /* substitute locR by locO */
  def subsLoc(locR: Loc, locO: Loc): Heap
  def domIn(loc: Loc): Boolean

  def isBottom: Boolean

  // toString
  def toStringAll: String
  def toStringLoc(loc: Loc): Option[String]
}

object Heap {
  val Bot: Heap = new DHeap(HashMap[Loc, Obj]())
  def apply(map: Map[Loc, Obj]): Heap = new DHeap(map)
}

class DHeap(val map: Map[Loc, Obj]) extends Heap {
  /* partial order */
  def <=(that: Heap): Boolean = {
    if (this.map eq that.map) true
    else if (this.map.size > that.map.size) false
    else if (this.map.isEmpty) true
    else if (that.map.isEmpty) false
    else if (!(this.map.keySet subsetOf that.map.keySet)) false
    else that.map.forall((kv) => {
      val (l, obj) = kv
      this.map.get(l) match {
        case Some(thisObj) => thisObj <= obj
        case None => false
      }
    })
  }

  private def weakUpdated(m: Map[Loc, Obj], loc: Loc, newObj: Obj): Map[Loc, Obj] =
    m.get(loc) match {
      case Some(oldObj) => m.updated(loc, oldObj + newObj)
      case None => m.updated(loc, newObj)
    }

  /* join */
  def +(that: Heap): Heap = {
    if (this.map eq that.map) this
    else if (this.isBottom) that
    else if (that.isBottom) this
    else {
      val joinKeySet = this.map.keySet ++ that.map.keySet
      val joinMap = joinKeySet.foldLeft(HashMap[Loc, Obj]())((m, key) => {
        val joinObj = (this.map.get(key), that.map.get(key)) match {
          case (Some(obj1), Some(obj2)) => Some(obj1 + obj2)
          case (Some(obj1), None) => Some(obj1)
          case (None, Some(obj2)) => Some(obj2)
          case (None, None) => None
        }
        joinObj match {
          case Some(obj) => m.updated(key, obj)
          case None => m
        }
      })
      new DHeap(joinMap)
    }
  }

  /* meet */
  def <>(that: Heap): Heap = {
    if (this.map eq that.map) this
    else if (this.map.isEmpty) Heap.Bot
    else if (that.map.isEmpty) Heap.Bot
    else {
      val meet = that.map.foldLeft(this.map)(
        (m, kv) => kv match {
          case (k, v) => m.get(k) match {
            case None => m - k
            case Some(vv) => m + (k -> (v <> vv))
          }
        }
      )
      new DHeap(meet)
    }
  }

  /* lookup */
  def apply(loc: Loc): Option[Obj] = map.get(loc)

  def getOrElse(loc: Loc, default: Obj): Obj =
    this(loc) match {
      case Some(obj) => obj
      case None => default
    }

  /* heap update */
  def update(loc: Loc, obj: Obj): Heap = {
    // recent location
    loc.recency match {
      case Recent =>
        if (obj.isBottom) Heap.Bot
        else new DHeap(map.updated(loc, obj))
      case Old =>
        if (obj.isBottom) this(loc) match {
          case Some(_) => this
          case None => Heap.Bot
        }
        else new DHeap(weakUpdated(map, loc, obj))
    }
  }

  /* remove location */
  def remove(loc: Loc): Heap = {
    new DHeap(map - loc)
  }

  /* substitute locR by locO */
  def subsLoc(locR: Loc, locO: Loc): Heap = {
    if (this.map.isEmpty) this
    else {
      val newMap =
        this.map.foldLeft(Map[Loc, Obj]())((m, kv) => {
          val (l, obj) = kv
          m + (l -> obj.subsLoc(locR, locO))
        })
      new DHeap(newMap)
    }
  }

  def domIn(loc: Loc): Boolean = map.contains(loc)

  def isBottom: Boolean = this.map.isEmpty // TODO is really bottom?

  override def toString: String = {
    buildString(loc => loc match {
      case Loc(ProgramAddr(_), _) => true
      case GLOBAL | SINGLE_PURE_LOCAL => true
      case _ => false
    }).toString
  }

  def toStringAll: String = {
    buildString(_ => true).toString
  }

  private def buildString(filter: Loc => Boolean): String = {
    val s = new StringBuilder
    this match {
      case Heap.Bot => s.append("⊥Heap")
      case _ => {
        val sortedSeq =
          map.toSeq.filter { case (loc, _) => filter(loc) }
            .sortBy { case (loc, _) => loc }
        sortedSeq.map {
          case (loc, obj) => s.append(toStringLoc(loc, obj)).append(LINE_SEP)
        }
      }
    }
    s.toString
  }

  def toStringLoc(loc: Loc): Option[String] = {
    map.get(loc).map(toStringLoc(loc, _))
  }

  private def toStringLoc(loc: Loc, obj: Obj): String = {
    val s = new StringBuilder
    val keyStr = loc.toString + " -> "
    s.append(keyStr)
    Useful.indentation(s, obj.toString, keyStr.length)
    s.toString
  }
}
