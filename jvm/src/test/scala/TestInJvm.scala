import io.rim99.nio4s.*
import io.rim99.nio4s.internal.JvmTcpListener
import org.scalatest.funspec.AnyFunSpec

import java.nio.ByteBuffer

class TestInJvm extends AnyFunSpec:
  describe("Test") {
    it("poll") {
      val ev = new JvmEventProcessor()
      val poller = new JvmPoller()
      val l = new JvmTcpListener(5454, poller, mockHttpProtocol)
      // common logic below 
      poller.addListener(l)
      while true do
        // TODO: schedule with timer
        val events = poller.poll()
        ev.process(events)
    }

    lazy val mockHttpProtocol = new Protocol {
      override def close(): Unit = ()
      
      override def handle(c: TcpChannel, buffer: ByteBuffer, size: Int): Int =
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
    }
  }

