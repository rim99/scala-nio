package io.apilet.http

import java.nio.ByteBuffer
import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

package object parser:

  @tailrec
  def findNextSP(buffer: ByteBuffer, count: Int = 0): Option[Int] =
    Try(buffer.get) match
      case Success(SpecialChars.SP) => Some(buffer.position() - 1) // exclude the SP in position
      case Success(_) => findNextSP(buffer, count + 1)
      case Failure(_) => None // assume it reaches the end of buffer

  @tailrec
  final def verifyNext(buf: ByteBuffer, chars: List[Char]): Boolean =
    chars match
      case Nil => true
      case h :: rest =>
        Try(buf.get) match
          case Success(i) if i == h => verifyNext(buf, rest)
          case _ => false
