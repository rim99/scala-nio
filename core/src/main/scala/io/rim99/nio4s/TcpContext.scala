package io.rim99.nio4s

import io.rim99.nio4s.internal.TcpConnection

import java.nio.ByteBuffer

class TcpContext(
  val conn: TcpConnection,
  val protocol: Protocol
):

  def close(): Unit =
    protocol.close()
    conn.close()

  def prepareForReading(): Unit =
    conn.prepareForReading(TcpContext.this)

  def handleInput(): Unit =
    Logger.trace("handling input")
    val buffer = ByteBuffer.allocate(256) // buffer pool needed here
    conn.read(buffer) match
      case Right(received) =>
        val consumed = protocol.handle(TcpContext.this, buffer, received)
        buffer.clear // ??? should it be cleared here
      // compare: consumed & received
      // while true when reading to buffer
      case Left(IOErrors.EOF) =>
        Logger.trace("Read failed: EOF")
        close()
      case Left(IOErrors.Exception(msg)) =>
        Logger.trace(s"Read failed: $msg")
        close()

  def handleOutput(response: ByteBuffer): Unit =
    conn.write(response).left.map { ex =>
      Logger.trace(s"Write failed: $ex")
      conn.close()
    }
