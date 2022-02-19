package io.apilet.http.parser

import io.apilet.nio4s.ProcessStatus
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.nio.ByteBuffer

class HttpMethodParserTest
  extends AnyFunSpec, Matchers, BeforeAndAfterEach:

  describe("HttpMethodParser") {

    val parser = new HttpMethodParser

    it("should parse all HTTP methods") {
      val verified = HttpMethod.values.forall { method =>
        val buf = ByteBuffer.wrap(method.name.getBytes("UTF-8"))

        val ret = parser.parse(buf)

        val ok = ret.contains(method)

        if !ok then println(s"${method.name} is not supported")
        else ()

        ok
      }

      verified shouldBe true
    }

    it("should return None if method is not supported") {
      val method = "not-supported"
      val buf = ByteBuffer.wrap(method.getBytes("UTF-8"))

      val ret = parser.parse(buf)

      ret shouldBe None
    }
  }

