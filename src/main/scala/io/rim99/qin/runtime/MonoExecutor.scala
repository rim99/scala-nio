package io.rim99.qin.runtime

import scala.concurrent.ExecutionContext

/** Process events within single thread This is for scala Promise instances
  * usage
  */
class MonoExecutor extends ExecutionContext:

  def execute(runnable: Runnable): Unit = ???

  def reportFailure(@deprecatedName("t") cause: Throwable): Unit = ???
