package io.rim99.nio4s

import java.nio.ByteBuffer

trait Protocol:
  def handleRead(c: TcpContext, buffer: ByteBuffer, size: Int): ProcessStatus
  def handleReadError(c: TcpContext, error: IOError): Unit
  def handleWriteError(c: TcpContext, error: IOError): Unit
  def close(): Unit

enum ProcessStatus:
  case NeedMore, Finished

trait ProtocolFactory:
  def spawn: Protocol
