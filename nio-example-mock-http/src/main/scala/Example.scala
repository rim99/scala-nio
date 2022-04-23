import io.rim99.nio4s.*
import scribe.trace as log

import java.nio.ByteBuffer

object Example extends App:

  lazy val mockHttpProtocolFactory = new ProtocolFactory:

    override def spawn: Protocol = new Protocol:
      override def close(): Unit = ()

      override def handleReadError(c: TcpContext, error: IOError): Unit =
        error match
          case IOErrors.EOF => log("Read failed: EOF")
          case IOErrors.Exception(msg) => log(s"Read error: ${msg}")

      override def handleWriteError(c: TcpContext, error: IOError): Unit =
        log(s"Write failed: $error")

      override def handleRead(
        c: TcpContext,
        buffer: ByteBuffer,
        size: Int
      ): ProcessStatus =
        if size == 0 then
          log("Read size is ZERO:0") // probably nothing to care
          c.close()
        else
          val response =
            "HTTP/1.1 200 OK\r\nServer: Nio4s\r\nConnection: keep-alive\r\nContent-Length: 5\r\n\r\nHello".getBytes
          val r = ByteBuffer.wrap(response)
          c.send(r)
        ProcessStatus.Finished

  val connMgr = new JvmConnectionManager(WorkModes.Light)
  connMgr.addListener(5454, mockHttpProtocolFactory)
  connMgr.await()
