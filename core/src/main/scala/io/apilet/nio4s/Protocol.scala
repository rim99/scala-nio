package io.apilet.nio4s

import java.nio.ByteBuffer

trait Protocol:
  def handle(c: TcpContext, buffer: ByteBuffer, size: Int): Int
  def close(): Unit

trait ProtocolFactory:
  def spawn: Protocol
