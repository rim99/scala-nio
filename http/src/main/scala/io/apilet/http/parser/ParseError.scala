package io.apilet.http.parser

sealed trait ParseError

object ParseErrors:
  object HttpMethodNotImplemented extends ParseError
  object HttpVersionNotSupported extends ParseError
  object URITooLarge extends ParseError
  object HeadersTooMany extends ParseError
  object BadRequest extends ParseError
