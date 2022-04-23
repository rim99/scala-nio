package io.apilet.http.parser

object SpecialChars:
  val SP: Char = ' '
  val CR: Char = '\r'
  val LF: Char = '\n'
  val Colon: Char = ':'
  val DOT: List[Char] = List('.')
  val HTTP_ : List[Char] = List('H','T','T','P', '/')
  val CRLF: List[Char] = List(SpecialChars.CR, SpecialChars.LF)
  val doubleCRLF: List[Char] = List(SpecialChars.CR, SpecialChars.LF, SpecialChars.CR, SpecialChars.LF)
