package cucumber.api.scalatest

import org.scalatest._

class TestCucumberFeatures extends CucumberSuite {

  override val nestedSuites = Vector(new CucumberRun, new CucumberRun)

}

class CucumberRun extends TestSuite { suite =>
  final override val testNames: Set[String] = Set("Given A", "When B", "Then C")

  final protected override def runTest(testName: String, args: Args): Status = {
    println(s"-> ${testName}")
    SucceededStatus
  }
}