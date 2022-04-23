package io.apilet.http.parser

import io.apilet.nio4s.ProcessStatus

import java.nio.ByteBuffer
import scala.annotation.tailrec
import scala.collection.mutable
import scala.util.{Failure, Success, Try}

private[parser] abstract class HttpRequestBuilder(
                                                   val buffer: ByteBuffer,
                                                   val httpMethod: HttpMethod,
                                                   val httpVersion: HttpVersion,
                                                   val uriOffset: Int,
                                                   val uriLength: Int
):

  def getURI: String =
    val uriBuf = Array.ofDim[Byte](uriLength)
    buffer.position(uriOffset)
    buffer.get(uriBuf, 0, uriLength)
    new String(uriBuf)

  def process: Either[ParseError, ProcessStatus]

/** For HTTP 1.1 protocol, this is to parse request headers & possible body
  *
  * @param buffer
  * @param httpMethod
  * @param httpVersion
  * @param uriOffset
  * @param uriLength
  */
final class Http11RequestBuilder(
                                  override val buffer: ByteBuffer,
                                  override val httpMethod: HttpMethod,
                                  override val httpVersion: HttpVersion,
                                  override val uriOffset: Int,
                                  override val uriLength: Int
) extends HttpRequestBuilder(
  buffer,
      httpMethod,
      httpVersion,
      uriOffset,
      uriLength
    ):

  private val headersBuffer = mutable.ListBuffer.empty[(Http11Request.HeaderName, Http11Request.HeaderValue)]

  lazy private val headersMap = headersBuffer.toMap




  @tailrec
  private def doProcess(
    state: Http11Request.State,
    headerName: Option[Http11Request.HeaderName] = None,
    headerValueStartPos: Int = 0,
    headerValue: Option[Http11Request.HeaderValue] = None,
  ): Either[ParseError, ProcessStatus] =
    state match {
      case Http11Request.State.FieldName =>
        val startPos = buffer.position()
        Try(buffer.get) match
          case Success(SpecialChars.CR) => doProcess(Http11Request.State.HeadersEnd)
          case Success(_) =>
            buffer.search(SpecialChars.Colon) match
              case Some(endPos) =>
                val headerName = buffer.getString(startPos, endPos)
                doProcess(Http11Request.State.OWS1, headerName = Some(headerName))
              case None => Left(ParseErrors.HeadersTooMany)
          case Failure(ex) => Left(ParseErrors.HeadersTooMany)
      case Http11Request.State.OWS1 =>
        val skipped = buffer.skip(SpecialChars.SP)
        skipped match
          case Some(startPos) => doProcess(
            state = Http11Request.State.FieldValue,
            headerName = headerName,
            headerValueStartPos = startPos
          )
          case None => Left(ParseErrors.HeadersTooMany)
      case Http11Request.State.FieldValue =>
        val found = buffer.searchAny(SpecialChars.SP, SpecialChars.CR)
        ????
        Left(ParseErrors.HeadersTooMany)
      case Http11Request.State.OWS2 =>
        Left(ParseErrors.HeadersTooMany)
      case Http11Request.State.FieldEnd =>
        Left(ParseErrors.HeadersTooMany)
      case Http11Request.State.HeadersEnd =>
        Left(ParseErrors.HeadersTooMany)
      case Http11Request.State.Body =>
        Left(ParseErrors.HeadersTooMany)
    }


  def process: Either[ParseError, ProcessStatus] = doProcess(state = Http11Request.State.FieldName)

  def appendBody(buf: ByteBuffer): Either[ParseError, ProcessStatus] = ???

object Http11Request:

  enum State:
    case FieldName, OWS1, FieldValue, OWS2, FieldEnd, HeadersEnd, Body

  type HeaderName = String
  type HeaderValue = String
