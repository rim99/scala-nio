package io.rim99.nio4s

import java.nio.ByteBuffer
import java.nio.channels.{SelectionKey, Selector, ServerSocketChannel, SocketChannel}
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

class JvmEventProcessor extends EventProcessor:
  override def processAcceptableEvents(a: List[AcceptableEvent]): Unit = a.foreach { ev =>
    val key = ev.asInstanceOf[JvmAcceptableEvent].key
    val selector = key.selector
    val listenSock = key.channel().asInstanceOf[ServerSocketChannel]
    val client = listenSock.accept
    client.configureBlocking(false)
    val conn = new JvmTcpConnection(client)
    client.register(selector, SelectionKey.OP_READ, conn)
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
    override def reportFailure(cause: Throwable): Unit = println(cause)
  }

  def answerWithEcho(buffer: ByteBuffer, key: SelectionKey): Unit =
    // process
    val conn = key.attachment().asInstanceOf[JvmTcpConnection]
    conn.read(buffer)
      // TODO: important: do we really need this future?
      .map { _ =>
          if new String(buffer.array).trim == "STOP" then
            conn.close()
            System.out.println("Not accepting client messages anymore")
          else
            Try {
              val r = ByteBuffer.wrap(response)
              conn.write(r)
            } match {
              case Success(_) => ()
              case Failure(_) =>
                conn.close()
                key.cancel()
            }
            buffer.clear
          ()
      }

