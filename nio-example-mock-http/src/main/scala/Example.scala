import io.apilet.nio4s.*

import java.nio.ByteBuffer

object Example extends App:

  lazy val mockHttpProtocolFactory = new ProtocolFactory:

    override def spawn: Protocol = new Protocol:
      override def close(): Unit = ()

      override def handleReadError(c: TcpContext, error: IOError): Unit =
        error match {
          case IOErrors.EOF => scribe.info("Read failed: EOF")
          case IOErrors.Exception(msg) => scribe.info(s"Read error: ${msg}")
        }
      override def handleWriteError(c: TcpContext, error: IOError): Unit =
        scribe.info(s"Write failed: $error")

      override def handleRead(c: TcpContext, buffer: ByteBuffer, size: Int): Boolean =
        if size == 0 then
          scribe.info("Read size is ZERO:0") // probably nothing to care
          c.close()
        else
          val response =
            "HTTP/1.1 200 OK\r\nServer: Nio4s\r\nConnection: keep-alive\r\nContent-Length: 5\r\n\r\nHello".getBytes
          val r = ByteBuffer.wrap(response)
          c.send(r)
        false

  val connMgr = new JvmConnectionManager(WorkModes.Light)
  connMgr.addListener(5454, mockHttpProtocolFactory)
  connMgr.await()
