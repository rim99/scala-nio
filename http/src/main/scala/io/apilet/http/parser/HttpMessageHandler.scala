package io.apilet.http.parser

import io.apilet.nio4s.ProcessStatus

import java.nio.ByteBuffer

trait HttpMessageHandler:
  val parserState: ParserState

  def parse(
    buf: ByteBuffer,
    limit: Int
  ): (Int, Either[ParseError, ProcessStatus])

trait StartLineHandler extends HttpMessageHandler:
  override val parserState = ParserStates.StartLine

class StatusLineHandler extends StartLineHandler:

  override def parse(
    buf: ByteBuffer,
    limit: Int
  ): (Int, Either[ParseError, ProcessStatus]) =
    (0, Right(ProcessStatus.Finished))

class HeadersHandler extends HttpMessageHandler:
  override val parserState = ParserStates.Headers

  override def parse(
    buf: ByteBuffer,
    limit: Int
  ): (Int, Either[ParseError, ProcessStatus]) =
    (0, Right(ProcessStatus.Finished))

class BodyHandler extends HttpMessageHandler:
  override val parserState = ParserStates.Body

  override def parse(
    buf: ByteBuffer,
    limit: Int
  ): (Int, Either[ParseError, ProcessStatus]) =
    (0, Right(ProcessStatus.Finished))

object SpecialChars:
  val SP: Char = ' '
  val CR: Char = '\r'
  val LF: Char = '\n'
  val DOT: List[Char] = List('.')
  val HTTP_ : List[Char] = List('H','T','T','P', '/')
  val CRLF: List[Char] = List(SpecialChars.CR, SpecialChars.LF)
  val doubleCRLF: List[Char] = List(SpecialChars.CR, SpecialChars.LF, SpecialChars.CR, SpecialChars.LF)
