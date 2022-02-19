package io.apilet.http.parser

enum HttpVersion(val content: List[Char]):
  case HTTP11 extends HttpVersion(List('H','T','T','P','/','1','.','1'))
