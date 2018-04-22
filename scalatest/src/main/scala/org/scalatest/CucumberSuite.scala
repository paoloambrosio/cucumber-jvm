package org.scalatest

abstract class CucumberSuite extends CucumberSuiteLike {

  override def toString: String = Suite.suiteToString(None, this)
}
