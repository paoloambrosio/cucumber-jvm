package org.scalatest

import cucumber.api.event._
import cucumber.api.formatter.Formatter
import cucumber.runtime.io.{MultiLoader, ResourceLoaderClassFinder}
import cucumber.runtime.{Runtime, RuntimeOptions}
import org.scalatest.events._

abstract class CucumberSuite  extends Suite { thisSuite =>

  // Reporter also supports rerunner... can we use that?!

  override def run(testName: Option[String], args: Args): Status = {
    val status = new ScalaTestStatefulStatus

    val runtime = cucumberRuntime()
    new CucumberEventListener(args).register(runtime.getEventBus)

    args.reporter(SuiteStarting(args.tracker.nextOrdinal(), "suitename", "suiteid", None)) // MOVE

    runtime.run()

    args.reporter(SuiteCompleted(args.tracker.nextOrdinal(), "suitename", "suiteid", None)) // MOVE

    status.setCompleted()

    if (runtime.exitStatus() != 0x0)
      status.setFailed()

    status
  }

  private def cucumberRuntime(): Runtime = {
    val classLoader = getClass.getClassLoader
    val resourceLoader = new MultiLoader(classLoader)
    val classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader)
    val runtimeOptions = new RuntimeOptions("--glue java8 classpath:features") // TODO
    new Runtime(resourceLoader, classFinder, classLoader, runtimeOptions)
  }

  private class ScalaTestFormatter extends Formatter {

    override def setEventPublisher(publisher: EventPublisher): Unit = {
      publisher.registerHandlerFor(classOf[TestCaseStarted], (_: TestCaseStarted) => ())
    }
  }
}

class CucumberEventListener(args: Args) {

  import args._

  def register(publisher: EventPublisher): Unit = {
    publisher.registerHandlerFor(classOf[TestStepStarted], handleTestStepStarted)
    publisher.registerHandlerFor(classOf[TestStepFinished], handleTestStepFinished)
  }

  private val handleTestStepStarted: EventHandler[TestStepStarted] = event => {
    reporter(TestStarting(tracker.nextOrdinal(), "suitename", "suiteid", None, event.testStep.getStepLocation, event.testStep.getStepText))
  }

  private val handleTestStepFinished: EventHandler[TestStepFinished] = event => {
    reporter(TestSucceeded(tracker.nextOrdinal(), "suitename", "suiteid", None, event.testStep.getStepLocation, event.testStep.getStepText, Vector.empty))
  }

  /*
  //    report(RunStarting(tracker.nextOrdinal(), 1, ConfigMap.empty))
  report(SuiteStarting(tracker.nextOrdinal(), "suitename", "suiteid", None))
  report(TestStarting(tracker.nextOrdinal(), "suitename", "suiteid", None, "test1", "testtext1"))
  report(TestSucceeded(tracker.nextOrdinal(), "suitename", "suiteid", None, "test1", "testtext1", Vector.empty, None))
  report(TestStarting(tracker.nextOrdinal(), "suitename", "suiteid", None, "test2", "testtext2"))
  report(TestFailed(tracker.nextOrdinal(), "message", "suitename", "suiteid", None, "test2", "testtext2", Vector.empty))
  report(SuiteCompleted(tracker.nextOrdinal(), "suitename", "suiteid", None))
  //    report(RunCompleted(tracker.nextOrdinal()))
*/
}
