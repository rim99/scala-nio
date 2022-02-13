package io.apilet.http.parser

sealed trait ParserState:
  val next: Option[ParserState]

object ParserStates:

  object StartLine extends ParserState:
    override val next: Option[ParserState] = Some(Headers)

  object Headers extends ParserState:
    override val next: Option[ParserState] = Some(Body)

  object Body extends ParserState:
    override val next: Option[ParserState] = None
