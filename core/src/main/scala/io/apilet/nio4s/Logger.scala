package io.apilet.nio4s

object Logger:
  private val level: Level = None

  trait Level
  case object None extends Level
  case object Trace extends Level

  def trace(msg: => String): Unit =
    level match
      case None => ()
      case Trace => println(s"log-trace: ${msg}")
