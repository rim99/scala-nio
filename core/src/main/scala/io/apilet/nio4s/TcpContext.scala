package io.apilet.nio4s

import io.apilet.nio4s.internal.TcpConnection

import java.nio.ByteBuffer
import scala.annotation.tailrec

class TcpContext(
  val conn: TcpConnection,
  val protocol: Protocol
):
  private val bufferPool = conn.worker.bufferPool

  // TODO: we should provided more method about conn states (eg. is pending, is connected, half-shutdown etc)

  def close(): Unit =
    // TODO: close in async manner
    protocol.close()
    conn.close()

  def prepareForReading(): Unit =
    conn.prepareForReading(TcpContext.this)

  def handleInput(): Unit =
    @tailrec
    def doRead(buf: ByteBuffer): Unit =
      conn.read(buf) match
        case Right(received) =>
          val potentialNexRound = !buf.hasRemaining && received > 0
          val needMore = protocol.handleRead(TcpContext.this, buf, received)
          (needMore, potentialNexRound) match
            case (true, true) =>
              buf.clear()
              doRead(buf)
            case (true, false) => () // TODO: create read event
            case (false, _) => ()
        case Left(e) =>
          protocol.handleReadError(TcpContext.this, e)
          close() // may have tail data to send, should be called later
    Logger.trace("handling input")
    val buf = bufferPool.getBuffer
    doRead(buf)
    bufferPool.recycleBuffer(buf)

  def handleOutput(response: ByteBuffer): Unit =
    // TODO: create write event
    conn.write(response).left.map { error =>
      protocol.handleWriteError(TcpContext.this, error)
      conn.close()
    }
