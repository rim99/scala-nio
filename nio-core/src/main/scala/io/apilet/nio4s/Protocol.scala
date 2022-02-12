package io.apilet.nio4s

import java.nio.ByteBuffer

trait Protocol:
  def handleRead(c: TcpContext, buffer: ByteBuffer, size: Int): Boolean
  def handleReadError(c: TcpContext, error: IOError): Unit
  def handleWriteError(c: TcpContext, error: IOError): Unit
  def close(): Unit

trait ProtocolFactory:
  def spawn: Protocol
