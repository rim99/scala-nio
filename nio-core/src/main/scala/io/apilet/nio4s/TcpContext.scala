package io.apilet.nio4s

import io.apilet.nio4s.internal.TcpConnection

import java.nio.ByteBuffer
import scala.annotation.tailrec
import scala.collection.mutable

class TcpContext(
  val conn: TcpConnection,
  val protocol: Protocol
):
  private val bufferPool = conn.worker.bufferPool
  private val writingQueue = mutable.ArrayDeque.empty[ByteBuffer]

  // TODO: we should provided more method about conn states (eg. is pending, is connected, half-shutdown etc)

  def close(): Unit =
    // TODO: close in async manner
    protocol.close()
    conn.close()

  def prepare(): Unit =
    conn.prepare(TcpContext.this)

  def send(response: ByteBuffer): Unit =
    if !writeNow(response) then writingQueue.append(response)
    else ()

  private[nio4s] def handleInput(): Unit =
    @tailrec
    def doRead(buf: ByteBuffer): Unit =
      conn.read(buf) match
        case Right(received) =>
          lazy val potentialNexRound = !buf.hasRemaining && received > 0
          val needMore = protocol.handleRead(TcpContext.this, buf, received)
          if needMore && potentialNexRound then
            buf.clear()
            doRead(buf)
          else ()
        case Left(e) =>
          protocol.handleReadError(TcpContext.this, e)
          close() // may have tail data to send, should be called later
    Logger.trace("handling input")
    val buf = bufferPool.getBuffer
    doRead(buf)
    bufferPool.recycleBuffer(buf)

  private[nio4s] def handleOutput(): Unit =
    writingQueue.removeHeadWhile(writeNow)

  private[nio4s] def writeNow(buf: ByteBuffer): Boolean =
    conn.write(buf) match
      case Right(sent) => !buf.hasRemaining
      case Left(err) =>
        protocol.handleWriteError(TcpContext.this, err)
        conn.close()
        false
