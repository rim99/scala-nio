import io.rim99.nio4s.*
import org.scalatest.funspec.AnyFunSpec

class TestInJvm extends AnyFunSpec:
  describe("Test") {
    it("poll") {
      val ev = new JvmEventProcessor()
      val poller = new JvmPoller()
      val l = new JvmTcpListener(5454, poller)
      // common logic below 
      poller.addListener(l)
      while true do
        // TODO: schedule with timer
        val events = poller.poll()
        ev.process(events)
    }
  }

