package io.rim99.nio4s

import io.rim99.nio4s.TcpConnection

import java.net.{InetAddress, SocketOption}
import java.nio.ByteBuffer
import java.nio.channels.{SelectionKey, Selector, SocketChannel}
import java.util.concurrent.TimeUnit
import scala.concurrent.Future
import scala.util.Try
import scala.jdk.CollectionConverters.*

class JvmTcpConnection(
  val socket: SocketChannel,
  val selector: Selector
) extends TcpConnection:

  override def getLocalAddress: Option[InetAddress] =
    Try(Option(socket.socket().getLocalAddress)).toOption.flatten

  override def getRemoteAddress: Option[InetAddress] =
    Try(Option(socket.socket().getInetAddress)).toOption.flatten

  override def getLocalPort: Option[Int] =
    val port = socket.socket().getLocalPort
    Option.when(port != -1)(port)

  def getRemotePort: Option[Int] =
    val port = socket.socket().getPort
    Option.when(port != -1)(port)

  override def getOption[T](name: SocketOption[T]): Try[T] =
    Try(socket.getOption(name))

  override def supportedOptions: Set[SocketOption[?]] =
    socket.supportedOptions().asScala.toSet

  override def setOption[T](
    name: SocketOption[T],
    value: T
  ): Try[Unit] = Try(socket.setOption(name, value))

  override def close(): Unit =
    // TODO: graceful shutdown
    socket.keyFor(selector).cancel()
    socket.close()

  override def isOpen: Boolean = !socket.socket().isClosed

  override def shutdownInput: Try[Unit] = Try(socket.shutdownInput())

  override def shutdownOutput: Try[Unit] = Try(socket.shutdownOutput())

  override def read(
    dst: ByteBuffer,
    timeout: Long,
    unit: TimeUnit
  ): Try[Int] =
    // TODO: generated read event and create timer event
    Try(socket.read(dst))

  override def readAll(
    dst: Array[ByteBuffer],
    offset: Int,
    length: Int,
    timeout: Long,
    unit: TimeUnit
  ): Try[Long] =
    Try(socket.read(dst))

  override def write(
    src: ByteBuffer,
    timeout: Long,
    unit: TimeUnit
  ): Try[Int] =
    Try(socket.write(src))

  override def writeAll(
    src: Array[ByteBuffer],
    offset: Int,
    length: Int,
    timeout: Long,
    unit: TimeUnit
  ): Try[Long] =
    Try(socket.write(src))

  override def prepareForReading(): Unit =
    socket.register(selector, SelectionKey.OP_READ, this)
    ()

  override def processInbound(): Unit =
    val buffer = ByteBuffer.allocate(256)
    read(buffer)
      .fold(
        { (ex: Throwable) =>
          Logger.trace(s"Read failed: $ex")
          close()
        },
        { (size: Int) =>
          if size == 0 then close()
          else if new String(buffer.array).trim == "STOP" then
            close()
            Logger.trace("Not accepting client messages anymore")
          else
            val response =
              "HTTP/1.1 200 OK\r\nServer: Nio4s\r\nConnection: keep-alive\r\nContent-Length: 5\r\n\r\nHello".getBytes
            val r = ByteBuffer.wrap(response)
            write(r).toEither.left.map { ex =>
              Logger.trace(s"Write failed: $ex")
              close()
            }
            buffer.clear
        }
      )
