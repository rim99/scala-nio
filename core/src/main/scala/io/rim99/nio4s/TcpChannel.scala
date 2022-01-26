package io.rim99.nio4s

import io.rim99.nio4s.internal.TcpConnection

import java.nio.ByteBuffer

trait InBound

class TcpChannel(
  val conn: TcpConnection,
  val protocol: Protocol
) extends InBound:

  def close(): Unit =
    protocol.close()
    conn.close()

  def prepareForReading(): Unit =
    conn.prepareForReading(TcpChannel.this)

  def handleInput(): Unit =
    Logger.trace("handling input")
    val buffer = ByteBuffer.allocate(256)
    conn.read(buffer) match
      case Right(received) =>
        val consumed = protocol.handle(TcpChannel.this, buffer, received)
        buffer.clear // ??? should it be cleared here
      // compare: consumed & received
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
