package io.apilet.nio4s

import io.apilet.nio4s.internal.TcpConnection

import java.nio.ByteBuffer

class TcpContext(
  val conn: TcpConnection,
  val protocol: Protocol
):
  private val bufferPool = conn.worker.bufferPool

  def close(): Unit =
    protocol.close()
    conn.close()

  def prepareForReading(): Unit =
    conn.prepareForReading(TcpContext.this)

  def handleInput(): Unit =
    Logger.trace("handling input")
    val buf = bufferPool.getBuffer
    conn.read(buf) match
      case Right(received) =>
        val consumed = protocol.handle(TcpContext.this, buf, received)
      // compare: consumed & received
      // while true when reading to buffer
      case Left(IOErrors.EOF) =>
        Logger.trace("Read failed: EOF")
        close()
      case Left(IOErrors.Exception(msg)) =>
        Logger.trace(s"Read failed: $msg")
        close()
    bufferPool.recycleBuffer(buf)    

  def handleOutput(response: ByteBuffer): Unit =
    // TODO: create write event
    conn.write(response).left.map { ex =>
      Logger.trace(s"Write failed: $ex")
      conn.close()
    }
