package io.rim99.nio4s

import scribe.Logger as SLog
import scribe.Level

object Logger {
  private val logger = SLog("Nio4s").withMinimumLevel(Level.Info)
  def trace(msg: => String): Unit = logger.trace(msg)
}
