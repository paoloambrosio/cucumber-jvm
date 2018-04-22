package org.scalatest

trait CucumberSuiteLike extends TestSuite { suite =>

  final protected override def runTest(testName: String, args: Args): Status = {
    println(s"%%%%%%%%%%%%%%%%%%% ${testName}")
    SucceededStatus
  }

  final override def testNames: Set[String] = Set("A")

  override def nestedSuites: collection.immutable.IndexedSeq[Suite] = Vector(new TestSuite { suite =>
    final override def testNames: Set[String] = Set("B")

    final protected override def runTest(testName: String, args: Args): Status = {
      println(s"&&&&&&&&&&&&&&&&&&& ${testName}")
      SucceededStatus
    }

    override def toString: String = "WOW"
  })

}
