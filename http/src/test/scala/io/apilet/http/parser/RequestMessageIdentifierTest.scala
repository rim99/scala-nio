package io.apilet.http.parser

import io.apilet.nio4s.ProcessStatus
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar.mock

import java.nio.ByteBuffer

class RequestMessageIdentifierTest
  extends AnyFunSpec, Matchers, BeforeAndAfterEach:

  describe("RequestMessageIdentifier") {

    it("should parse request line") {
      val requestLine = "GET /abc HTTP/1.1\r\n"
      val buf = ByteBuffer.wrap(requestLine.getBytes)

      val identifier = new RequestMessageIdentifier(buf)
      val ret = identifier.process
      val parser = ret.toOption.get

      parser.httpMethod shouldEqual HttpMethod.GET
      parser.httpVersion shouldEqual HttpVersion.HTTP11
      parser.getURI shouldEqual "/abc"
      parser.initialBuffer shouldBe buf
    }

    it("should return HttpMethodNotImplemented if the HTTP method in buffer is unknown") {
      val requestLine = "NoSuchMethod /abc HTTP/1.1\r\n"
      val buf = ByteBuffer.wrap(requestLine.getBytes)

      val identifier = new RequestMessageIdentifier(buf)
      val ret = identifier.process

      ret shouldEqual Left(ParseErrors.HttpMethodNotImplemented)
    }

    it("should return URITooLarge if the URI doesn't end in the buffer") {
      val requestLine = "POST /ab"
      val buf = ByteBuffer.wrap(requestLine.getBytes)

      val identifier = new RequestMessageIdentifier(buf)
      val ret = identifier.process

      ret shouldEqual Left(ParseErrors.URITooLarge)
    }

    it("should return HttpVersionNotSupported if the HTTP Version is not defined") {
      val requestLine = "POST /abc HTTP/000"
      val buf = ByteBuffer.wrap(requestLine.getBytes)

      val identifier = new RequestMessageIdentifier(buf)
      val ret = identifier.process

      ret shouldEqual Left(ParseErrors.HttpVersionNotSupported)
    }

    it("should return BadRequest if there is no space after HTTP method") {
      val requestLine = "POST/abc HTTP/000"
      val buf = ByteBuffer.wrap(requestLine.getBytes)

      val identifier = new RequestMessageIdentifier(buf)
      val ret = identifier.process

      ret shouldEqual Left(ParseErrors.BadRequest)
    }

    it("should return BadRequest if there is more than 1 space after HTTP method") {
      val requestLine = "POST  /abc HTTP/000"
      val buf = ByteBuffer.wrap(requestLine.getBytes)

      val identifier = new RequestMessageIdentifier(buf)
      val ret = identifier.process

      ret shouldEqual Left(ParseErrors.BadRequest)
    }

    it("should return URITooLarge if there is no space after URI") {
      val requestLine = "POST /abcHTTP/000"
      val buf = ByteBuffer.wrap(requestLine.getBytes)

      val identifier = new RequestMessageIdentifier(buf)
      val ret = identifier.process

      ret shouldEqual Left(ParseErrors.URITooLarge)
    }

    it("should return HttpVersionNotSupported if there is more than 1 space after URI") {
      val requestLine = "POST /abc  HTTP/000"
      val buf = ByteBuffer.wrap(requestLine.getBytes)
      
      val identifier = new RequestMessageIdentifier(buf)
      val ret = identifier.process

      ret shouldEqual Left(ParseErrors.HttpVersionNotSupported)
    }

    it("should return BadRequest if there is no CFLR after HTTP version") {
      val requestLine = "GET /abc HTTP/1.1"
      val buf = ByteBuffer.wrap(requestLine.getBytes)

      val identifier = new RequestMessageIdentifier(buf)
      val ret = identifier.process

      ret shouldEqual Left(ParseErrors.BadRequest)
    }
  }