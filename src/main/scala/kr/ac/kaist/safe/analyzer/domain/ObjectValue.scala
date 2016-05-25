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

trait ObjectValue {
  val value: Value
  val writable: AbsBool
  val enumerable: AbsBool
  val configurable: AbsBool

  /* partial order */
  def <=(that: ObjectValue): Boolean
  /* not a partial order */
  def </(that: ObjectValue): Boolean
  /* join */
  def +(that: ObjectValue): ObjectValue
  /* meet */
  def <>(that: ObjectValue): ObjectValue

  def isBottom: Boolean

  def copyWith(newValue: Value): ObjectValue
  def copyWithWritable(newWritable: AbsBool): ObjectValue
  def copyWithEnumerable(newEnumerable: AbsBool): ObjectValue
  def copyWithConfigurable(newConfigurable: AbsBool): ObjectValue
}

case class DefaultObjectValue(
    value: Value,
    writable: AbsBool,
    enumerable: AbsBool,
    configurable: AbsBool
) extends ObjectValue {
  /* partial order */
  def <=(that: ObjectValue): Boolean = {
    this.value <= that.value &&
      this.writable <= that.writable &&
      this.enumerable <= that.enumerable &&
      this.configurable <= that.configurable
  }

  /* not a partial order */
  def </(that: ObjectValue): Boolean = {
    this.value </ that.value ||
      this.writable </ that.writable ||
      this.enumerable </ that.enumerable ||
      this.configurable </ that.configurable
  }

  /* join */
  def +(that: ObjectValue): ObjectValue = {
    DefaultObjectValue(
      this.value + that.value,
      this.writable + that.writable,
      this.enumerable + that.enumerable,
      this.configurable + that.configurable
    )
  }

  /* meet */
  def <>(that: ObjectValue): ObjectValue = {
    DefaultObjectValue(
      this.value <> that.value,
      this.writable <> that.writable,
      this.enumerable <> that.enumerable,
      this.configurable <> that.configurable
    )
  }

  def isBottom: Boolean =
    this.value.isBottom && this.writable.isBottom && this.enumerable.isBottom && this.configurable.isBottom

  def copyWith(newValue: Value): ObjectValue =
    DefaultObjectValue(newValue, writable, enumerable, configurable)
  def copyWithWritable(newWritable: AbsBool): ObjectValue =
    DefaultObjectValue(value, newWritable, enumerable, configurable)
  def copyWithEnumerable(newEnumerable: AbsBool): ObjectValue =
    DefaultObjectValue(value, writable, newEnumerable, configurable)
  def copyWithConfigurable(newConfigurable: AbsBool): ObjectValue =
    DefaultObjectValue(value, writable, enumerable, newConfigurable)
}