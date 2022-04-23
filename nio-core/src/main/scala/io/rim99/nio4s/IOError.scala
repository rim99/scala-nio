package io.rim99.nio4s

sealed trait IOError

object IOErrors:
  object EOF extends IOError
  case class Exception(msg: String) extends IOError
