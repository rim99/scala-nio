import io.apilet.nio4s.internal.JvmTcpListener
import io.apilet.nio4s.*
import org.scalatest.funspec.AnyFunSpec

import java.nio.ByteBuffer
import java.util.concurrent.locks.ReentrantLock

class TestInJvm extends AnyFunSpec:
  describe("Test") {
    it("test") {
      assert(1 == 1)
    }
  }

