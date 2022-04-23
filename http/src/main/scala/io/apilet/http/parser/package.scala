package io.apilet.http

import java.nio.ByteBuffer
import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

package object parser:

  object Op:
    def equal[T](a: T, b: T): Boolean = a == b
    def notEqual[T](a: T, b: T): Boolean = a != b

  extension (buffer: ByteBuffer)

    @tailrec
    def findNext(op: (Char, Char) => Boolean, char: Char, count: Int = 0): Option[Int] =
      Try(buffer.get) match
        case Success(c) if c == char => Some(buffer.position() - 1) // exclude this "char" in position
        case Success(_) => findNext(op, char, count + 1)
        case Failure(_) => None // assume it reaches the end of buffer

    //def findNextSP(count: Int = 0): Option[Int] = findNext(Op.equal, SpecialChars.SP, count)

    def search(char: Char): Option[Int] = findNext(Op.equal, char)

    def skip(char: Char): Option[Int] = findNext(Op.notEqual, char)

    @tailrec
    final def verifyNext(chars: List[Char]): Boolean =
      chars match
        case Nil => true
        case h :: rest =>
          Try(buffer.get) match
            case Success(i) if i == h => verifyNext(rest)
            case _ => false

    def getString(startPos: Int, endPos: Int): String = "Not implemented"

