package io.rim99.nio4s

import java.nio.ByteBuffer

trait Protocol:
  def handle(c: TcpChannel, buffer: ByteBuffer, size: Int): Int 
  def close(): Unit
