package io.apilet.http.parser

import io.apilet.nio4s.ProcessStatus

import java.nio.ByteBuffer
import scala.annotation.tailrec

private[http] class ReentrantHttpParser(
  private val startLineHandler: StartLineHandler,
  private val headersHandler: HeadersHandler,
  private val bodyHandler: BodyHandler
):

  /** RFC7230 Compliance. Refer to:
    * https://datatracker.ietf.org/doc/html/rfc7230
    */
  private var currentHandler: HttpMessageHandler = startLineHandler
  private var processedBytes: Int = 0

  /** Reentrant parsing bytes
    * @param buf
    * @param limit
    * @return
    */
  @tailrec
  private def doParse(buf: ByteBuffer, limit: Int): Either[ParseError, ProcessStatus] =
    val (processed, handlerStatus) = currentHandler.parse(buf, limit)
    processedBytes += processed
    val parserStatus = (handlerStatus, currentHandler.parserState.next) match
      case (Right(ProcessStatus.Finished), Some(nextState)) =>
        val nextHandler = nextState match
          case ParserStates.StartLine => startLineHandler
          case ParserStates.Headers => headersHandler
          case ParserStates.Body => bodyHandler
        currentHandler = nextHandler
        Right(ProcessStatus.NeedMore)
      case _ => handlerStatus
    val rest = limit - processed
    if rest > 0 && parserStatus.contains(ProcessStatus.NeedMore) then doParse(buf, rest)
    else parserStatus

  def parse(buf: ByteBuffer, limit: Int): Either[ParseError, ProcessStatus] = doParse(buf, limit)

  def getProcessedBytes: Int = processedBytes

object HttpParser:

  def apply(mode: ParseMode): ReentrantHttpParser =
    val startLineHandler = mode match
      case ParseModes.Server => new RequestLineHandler
      case ParseModes.Client => new StatusLineHandler

    val headersHandler = new HeadersHandler
    val bodyHandler = new BodyHandler
    new ReentrantHttpParser(
      startLineHandler,
      headersHandler,
      bodyHandler
    )

sealed trait ParseMode

object ParseModes:
  object Server extends ParseMode
  object Client extends ParseMode
