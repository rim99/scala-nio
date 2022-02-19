package io.apilet.http.parser

import java.nio.ByteBuffer

class HttpRequestParser(
  val initialBuffer: ByteBuffer,
  val httpMethod: HttpMethod,
  val httpVersion: HttpVersion,
  val uriOffset: Int,
  val uriLength: Int
):

  def getURI: String =
    val uriBuf = Array.ofDim[Byte](uriLength)
    initialBuffer.position(uriOffset)
    initialBuffer.get(uriBuf, 0, uriLength)
    new String(uriBuf)
