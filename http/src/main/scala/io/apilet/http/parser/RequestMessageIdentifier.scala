package io.apilet.http.parser

import io.apilet.nio4s.ProcessStatus

import java.nio.ByteBuffer
import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

/** This is for identify the HTTP protocol version of the message, so the
  * following handlers can be decided.
  *
  *   - For HTTP protocol ver 1.1, it is responsible for
  *     - parse the request-line
  *   - For HTTP protocol ver 2, it is responsible for:
  *     - identify HTTP/2 connection preface
  */
class RequestMessageIdentifier(
  buffer: ByteBuffer,
  httpMethodParser: HttpMethodParser = HttpMethodParsers.instance
):

  @tailrec
  private def doParse(
    state: RequestLineHandler.State,
    httpMethod: Option[HttpMethod] = None,
    uriBegin: Int = 0,
    uriEnd: Int = 0,
    httpVersion: Option[HttpVersion] = None
  ): Either[ParseError, Http11RequestBuilder] =
    state match
      case RequestLineHandler.State.HttpMethod =>
        val parsed = httpMethodParser.parse(buffer)
        parsed match
          case Some(m) =>
            doParse(
              state = RequestLineHandler.State.AfterHttpMethod,
              httpMethod = Some(m)
            )
          case None => Left(ParseErrors.HttpMethodNotImplemented)
      case RequestLineHandler.State.AfterHttpMethod =>
        Try(buffer.get) match
          case Success(SpecialChars.SP) =>
            doParse(
              state = RequestLineHandler.State.URI,
              httpMethod = httpMethod
            )
          case _ => Left(ParseErrors.BadRequest)
      case RequestLineHandler.State.URI =>
        val uriBegin = buffer.position()
        Try(buffer.get) match
          case Success(SpecialChars.SP) => Left(ParseErrors.BadRequest)
          case Success(_) => buffer.search(SpecialChars.SP) match
            case Some(end) =>
              doParse(
                state = RequestLineHandler.State.HTTPVersion,
                httpMethod = httpMethod,
                uriBegin = uriBegin,
                uriEnd = end
              )
            case None => Left(ParseErrors.URITooLarge)
          case Failure(_) => Left(ParseErrors.BadRequest)
      case RequestLineHandler.State.HTTPVersion =>
        val hasHttp = buffer.verifyNext(SpecialChars.HTTP_)
        val majorVersion = Try(buffer.get)
        val hasDot = buffer.verifyNext(SpecialChars.DOT)
        val minorVersion = Try(buffer.get)
        val httpVersion: Option[HttpVersion] = Option
            .when(hasHttp && hasDot) {
              (majorVersion, minorVersion) match
                case (Success('1'), Success('1')) =>
                  Some(HttpVersion.HTTP11)
                case _ => None
            }
            .flatten
        httpVersion match
          case Some(version) =>
            doParse(
              state = RequestLineHandler.State.AfterHTTPVersion,
              httpMethod = httpMethod,
              uriBegin = uriBegin,
              uriEnd = uriEnd,
              httpVersion = Some(version)
            )
          case None =>
            Left(ParseErrors.HttpVersionNotSupported)
      case RequestLineHandler.State.AfterHTTPVersion =>
        val ok = buffer.verifyNext(SpecialChars.CRLF)
        if ok then
          val parser = new Http11RequestBuilder(
            buffer = buffer,
            httpMethod = httpMethod.get,
            httpVersion = httpVersion.get,
            uriOffset = uriBegin,
            uriLength = uriEnd - uriBegin
          )
          Right(parser)
        else Left(ParseErrors.BadRequest)


  def process: Either[ParseError, HttpRequestBuilder] = doParse(state = RequestLineHandler.State.HttpMethod)


object RequestLineHandler:

  enum State:
    case HttpMethod, AfterHttpMethod, URI, HTTPVersion, AfterHTTPVersion