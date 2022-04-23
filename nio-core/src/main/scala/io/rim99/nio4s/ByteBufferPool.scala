package io.rim99.nio4s

import java.nio.ByteBuffer
import scala.collection.mutable

class ByteBufferPool(poolSize: Int = 2048, bufSize: Int = 4096):
  // TODO: make these parameters can be configured by file/env parameters

  private val container = new mutable.ArrayDeque[ByteBuffer](poolSize)

  protected def newBuffer: ByteBuffer = ByteBuffer.allocateDirect(bufSize)

  def getBuffer: ByteBuffer = container.removeHeadOption().getOrElse(newBuffer)

  def recycleBuffer(buf: ByteBuffer): Unit =
    buf.clear
    container.prepend(buf)
