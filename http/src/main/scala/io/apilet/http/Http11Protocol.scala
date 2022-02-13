package io.apilet.http

import io.apilet.nio4s.{IOError, ProcessStatus, Protocol, TcpContext}

import java.nio.ByteBuffer

class Http11Protocol extends Protocol:
  def handleRead(c: TcpContext, buffer: ByteBuffer, size: Int): ProcessStatus = ???
  def handleReadError(c: TcpContext, error: IOError): Unit = ???
  def handleWriteError(c: TcpContext, error: IOError): Unit = ???
  def close(): Unit = ???
