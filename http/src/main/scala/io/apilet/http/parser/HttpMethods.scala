package io.apilet.http.parser

import java.nio.ByteBuffer
import scala.annotation.tailrec

enum HttpMethod(val name: String, val rest: List[Char]):
  /* Compliance: https://datatracker.ietf.org/doc/html/rfc7231#section-4 */
  case GET extends HttpMethod("GET", List('E', 'T'))
  case POST extends HttpMethod("POST", List('S', 'T'))
  case PUT extends HttpMethod("PUT", List('T'))
  case DELETE extends HttpMethod("DELETE", List('E', 'L', 'E', 'T', 'E'))
  case HEAD extends HttpMethod("HEAD", List('E', 'A', 'D'))
  case CONNECT extends HttpMethod("CONNECT", List('O', 'N', 'N', 'E', 'C', 'T'))
  case OPTIONS extends HttpMethod("OPTIONS", List('P', 'T', 'I', 'O', 'N', 'S'))
  case TRACE extends HttpMethod("TRACE", List('R', 'A', 'C', 'E'))

class HttpMethodParser:

  def parse(buf: ByteBuffer): Option[HttpMethod] =
    buf.get match
      case 'G' =>
        Option.when(buf.verifyNext(HttpMethod.GET.rest)) {
          HttpMethod.GET
        }
      case 'P' =>
        buf.get match
          case 'O' =>
            Option.when(buf.verifyNext(HttpMethod.POST.rest)) {
              HttpMethod.POST
            }
          case 'U' =>
            Option.when(buf.verifyNext(HttpMethod.PUT.rest)) {
              HttpMethod.PUT
            }
          case _ => None
      case 'D' =>
        Option.when(buf.verifyNext(HttpMethod.DELETE.rest)) {
          HttpMethod.DELETE
        }
      case 'H' =>
        Option.when(buf.verifyNext(HttpMethod.HEAD.rest)) {
          HttpMethod.HEAD
        }
      case 'C' =>
        Option.when(buf.verifyNext(HttpMethod.CONNECT.rest)) {
          HttpMethod.CONNECT
        }
      case 'O' =>
        Option.when(buf.verifyNext(HttpMethod.OPTIONS.rest)) {
          HttpMethod.OPTIONS
        }
      case 'T' =>
        Option.when(buf.verifyNext(HttpMethod.TRACE.rest)) {
          HttpMethod.TRACE
        }
      case _ => None

object HttpMethodParsers:
  val instance = new HttpMethodParser
