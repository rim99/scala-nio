package io.rim99.nio4s

import java.nio.ByteBuffer
import java.nio.channels.{SelectionKey, Selector, ServerSocketChannel, SocketChannel}
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

class JvmEventProcessor extends EventProcessor:
  override def processAcceptableEvents(a: List[AcceptableEvent]): Unit = a.foreach { e =>
    val event = e.asInstanceOf[JvmAcceptableEvent]
    val poller = event.poller
    val listenSock = event.key.channel().asInstanceOf[ServerSocketChannel]
    val newSock = listenSock.accept
    newSock.configureBlocking(false) //TODO: where to put the configuring logic
    val newConn = new JvmTcpConnection(newSock)
    poller.addForReading(newConn)
  }
  override def processConnectableEvents(a: List[ConnectableEvent]): Unit = ()
  override def processWritableEvents(a: List[WritableEvent]): Unit = ()
  override def processReadableEvents(a: List[ReadableEvent]): Unit = a.foreach { ev =>
    val k = ev.asInstanceOf[JvmReadableEvent].key
    answerWithEcho(buffer, k)
  }

  // TODO: refactor following

  val buffer = ByteBuffer.allocate(256)
  val response = "HTTP/1.1 200 OK\r\nServer: Nio4s\r\nConnection: keep-alive\r\nContent-Length: 5\r\n\r\nHello".getBytes

  given simpleEc: ExecutionContext = new ExecutionContext {
    override def execute(runnable: Runnable): Unit = runnable.run()
    override def reportFailure(cause: Throwable): Unit = Logger.trace(cause.toString)
  }

  def answerWithEcho(buffer: ByteBuffer, key: SelectionKey): Unit =
    // process
    val conn = key.attachment().asInstanceOf[JvmTcpConnection]
    conn.read(buffer)
      .fold(
        { (ex: Throwable) =>
          Logger.trace(s"Read failed: $ex")
          conn.close()
          key.cancel()
        },
        { (size: Int) =>
          if size == 0 then
            conn.close()
            key.cancel()
            ()
          else if new String(buffer.array).trim == "STOP" then
            conn.close()
            Logger.trace("Not accepting client messages anymore")
          else
            val r = ByteBuffer.wrap(response)
            conn.write(r).left.map { ex =>
              Logger.trace(s"Write failed: $ex")
              conn.close()
              key.cancel()
            }
            buffer.clear
        }
      )
