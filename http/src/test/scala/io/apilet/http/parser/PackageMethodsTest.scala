package io.apilet.http.parser

import org.scalatest.BeforeAndAfterEach
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.nio.ByteBuffer

class PackageMethodsTest
  extends AnyFunSpec, Matchers, BeforeAndAfterEach:

  describe("verifyNext") {
    it("should pass if chars are match with byte buffer") {
      val buf = ByteBuffer.wrap("abc".getBytes)
      val chars= List('a', 'b', 'c')

      val res = buf.verifyNext(chars)

      res shouldBe true
    }

    it("should not pass if chars aren't match with byte buffer") {
      val buf = ByteBuffer.wrap("abc".getBytes)
      val chars = List('a', 'b', 'd')

      val res = buf.verifyNext(chars)

      res shouldBe false
    }

    it("should not pass if byte buffer's size is not big enough") {
      val buf = ByteBuffer.wrap("x".getBytes)
      val chars = List('a', 'b', 'd')

      val res = buf.verifyNext(chars)

      res shouldBe false
    }
  }
