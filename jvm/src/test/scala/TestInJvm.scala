import io.rim99.nio4s.*
import io.rim99.nio4s.internal.JvmTcpListener
import org.scalatest.funspec.AnyFunSpec

import java.nio.ByteBuffer
import java.util.concurrent.locks.ReentrantLock

class TestInJvm extends AnyFunSpec:
  describe("Test") {
    it("test") {
      assert(1 == 1)
    }
  }

