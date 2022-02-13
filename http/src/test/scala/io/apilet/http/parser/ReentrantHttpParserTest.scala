package io.apilet.http.parser

import io.apilet.nio4s.ProcessStatus
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar.mock

import java.nio.ByteBuffer

class ReentrantHttpParserTest
  extends AnyFunSpec, Matchers, BeforeAndAfterEach:

  private val mockStartLineHandler = mock[StartLineHandler]
  private val mockHeadersHandler = mock[HeadersHandler]
  private val mockBodyHandler = mock[BodyHandler]

  private val startLineHandler = new StartLineHandler:

    override def parse(
      buf: ByteBuffer,
      limit: Int
    ): (Int, Either[ParseError, ProcessStatus]) =
      mockStartLineHandler.parse(buf, limit)

  private val headersHandler = new HeadersHandler:

    override def parse(
      buf: ByteBuffer,
      limit: Int
    ): (Int, Either[ParseError, ProcessStatus]) =
      mockHeadersHandler.parse(buf, limit)

  private val bodyHandler = new BodyHandler:

    override def parse(
      buf: ByteBuffer,
      limit: Int
    ): (Int, Either[ParseError, ProcessStatus]) =
      mockBodyHandler.parse(buf, limit)

  describe("ReentrantHttpParser") {

    it("should parse bytes with multiple handlers") {
      val bytes = mock[ByteBuffer]
      val limit = 21

      when(mockStartLineHandler.parse(bytes, 21)) thenReturn ((5, Right(ProcessStatus.Finished)))
      when(mockHeadersHandler.parse(bytes, 16)) thenReturn ((7, Right(ProcessStatus.Finished)))
      when(mockBodyHandler.parse(bytes, 9)) thenReturn ((9, Right(ProcessStatus.Finished)))

      val parser = new ReentrantHttpParser(
        startLineHandler,
        headersHandler,
        bodyHandler
      )

      val result = parser.parse(bytes, limit)

      result shouldEqual Right(ProcessStatus.Finished)
      parser.getProcessedBytes shouldEqual 21
    }

    it("should continue parsing if more bytes available") {
      val initialBytes = mock[ByteBuffer]
      val initLimit = 10
      val moreBytes = mock[ByteBuffer]
      val moreLimit = 11

      when(mockStartLineHandler.parse(initialBytes, 10)) thenReturn ((5, Right(ProcessStatus.Finished)))
      when(mockHeadersHandler.parse(initialBytes, 5)) thenReturn ((5, Right(ProcessStatus.NeedMore)))
      when(mockHeadersHandler.parse(moreBytes, 11)) thenReturn ((2, Right(ProcessStatus.Finished)))
      when(mockBodyHandler.parse(moreBytes, 9)) thenReturn ((9, Right(ProcessStatus.Finished)))

      val parser = new ReentrantHttpParser(
        startLineHandler,
        headersHandler,
        bodyHandler
      )

      val result1 = parser.parse(initialBytes, initLimit)
      result1 shouldEqual Right(ProcessStatus.NeedMore)
      parser.getProcessedBytes shouldEqual 10

      val result2 = parser.parse(moreBytes, moreLimit)
      result2 shouldEqual Right(ProcessStatus.Finished)
      parser.getProcessedBytes shouldEqual 21
    }

    it("should stop parsing bytes if error returned") {
      val error = mock[ParseError]
      val bytes = mock[ByteBuffer]
      val limit = 21

      when(mockStartLineHandler.parse(bytes, 21)) thenReturn ((5, Right(ProcessStatus.Finished)))
      when(mockHeadersHandler.parse(bytes, 16)) thenReturn ((7, Left(error)))

      val parser = new ReentrantHttpParser(
        startLineHandler,
        headersHandler,
        bodyHandler
      )

      val result = parser.parse(bytes, limit)
      result shouldEqual Left(error)
      parser.getProcessedBytes shouldEqual 12
    }

  }
