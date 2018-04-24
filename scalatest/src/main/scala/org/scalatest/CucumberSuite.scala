package org.scalatest

import cucumber.api.event.{EventPublisher, TestCaseStarted}
import cucumber.api.formatter.Formatter
import cucumber.runtime.io.{ClasspathResourceLoader, ResourceLoaderClassFinder}
import cucumber.runtime.{Runtime, RuntimeOptions}
import org.scalatest.events._

abstract class CucumberSuite  extends Suite { thisSuite =>

  // Reporter also supports rerunner... can we use that?!

  override def run(testName: Option[String], args: Args): Status = {
    val status = new ScalaTestStatefulStatus

    val report = args.reporter
    val tracker = args.tracker

//    report(RunStarting(tracker.nextOrdinal(), 1, ConfigMap.empty))
    report(SuiteStarting(tracker.nextOrdinal(), "suitename", "suiteid", None))
    report(TestStarting(tracker.nextOrdinal(), "suitename", "suiteid", None, "test1", "testtext1"))
    report(TestSucceeded(tracker.nextOrdinal(), "suitename", "suiteid", None, "test1", "testtext1", Vector.empty, None))
    report(TestStarting(tracker.nextOrdinal(), "suitename", "suiteid", None, "test2", "testtext2"))
    report(TestFailed(tracker.nextOrdinal(), "message", "suitename", "suiteid", None, "test2", "testtext2", Vector.empty))
    report(SuiteCompleted(tracker.nextOrdinal(), "suitename", "suiteid", None))
//    report(RunCompleted(tracker.nextOrdinal()))
    status.setFailed()
//    status.setCompleted()
    status
  }

  private def cucumberRuntime(): Runtime = {
    val classLoader = getClass.getClassLoader
    val resourceLoader = new ClasspathResourceLoader(classLoader)
    val classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader)
    val runtimeOptions = new RuntimeOptions("") // TODO
    new Runtime(resourceLoader, classFinder, classLoader, runtimeOptions)
  }

  private class ScalaTestFormatter extends Formatter {

    override def setEventPublisher(publisher: EventPublisher): Unit = {
      publisher.registerHandlerFor(classOf[TestCaseStarted], (_: TestCaseStarted) => ())
    }
  }
}
