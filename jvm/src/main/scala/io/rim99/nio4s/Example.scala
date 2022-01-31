package io.rim99.nio4s

import java.nio.ByteBuffer

object Example extends App:

  lazy val mockHttpProtocolFactory = new ProtocolFactory:

    override def spawn: Protocol = new Protocol:
      override def close(): Unit = ()

      override def handle(c: TcpContext, buffer: ByteBuffer, size: Int): Int =
        if size == 0 then
          Logger.trace("Read size is ZERO:0") // probably nothing to care
          c.close()
        else if new String(buffer.array).trim == "STOP" then
          c.close()
          Logger.trace("Not accepting client messages anymore")
        else
          val response =
            "HTTP/1.1 200 OK\r\nServer: Nio4s\r\nConnection: keep-alive\r\nContent-Length: 5\r\n\r\nHello".getBytes
          val r = ByteBuffer.wrap(response)
          c.handleOutput(r)
        1

  val connMgr = new JvmConnectionManager(1)
  connMgr.addListener(5454, mockHttpProtocolFactory)
  connMgr.await()
