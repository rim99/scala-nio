import io.rim99.nio4s.*
import org.scalatest.funspec.AnyFunSpec

import scala.util.{Failure, Success, Try}
import scala.jdk.CollectionConverters.*

class TestInJvm extends AnyFunSpec:
  describe("Test") {
    it("poll") {
      val ev = new JvmEventProcessor()
      val poller = new JvmPoller()
      val l = new JvmTcpListener(5454)
      // common logic below 
      poller.addListener(l)
      while (true) {
        // TODO: schedule with timer
        val events = poller.poll()
        ev.process(events)
      }
    }
  }

