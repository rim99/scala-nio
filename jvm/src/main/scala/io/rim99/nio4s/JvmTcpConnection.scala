package io.rim99.nio4s

import io.rim99.nio4s.TcpConnection

import java.net.{InetAddress, Socket, SocketOption}
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.util.concurrent.TimeUnit
import scala.concurrent.Future
import scala.util.Try
import scala.jdk.CollectionConverters.*

class JvmTcpConnection(val socket: SocketChannel) extends TcpConnection:

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

  def close(): Unit =
    // TODO: graceful shutdown
    socket.close()

  def isOpen: Boolean = !socket.socket().isClosed

  def shutdownInput: Try[Unit] = Try(socket.shutdownInput())

  def shutdownOutput: Try[Unit] = Try(socket.shutdownOutput())

  def read(dst: ByteBuffer, timeout: Long, unit: TimeUnit): Maybe[Int] =
    // TODO: generated read event and create timer event
    Try(socket.read(dst)).toEither

  def readAll(
    dst: Array[ByteBuffer],
    offset: Int,
    length: Int,
    timeout: Long,
    unit: TimeUnit
  ): Maybe[Long] =
    Try(socket.read(dst)).toEither

  def write(src: ByteBuffer, timeout: Long, unit: TimeUnit): Maybe[Int] =
    Try(socket.write(src)).toEither

  def writeAll(
    src: Array[ByteBuffer],
    offset: Int,
    length: Int,
    timeout: Long,
    unit: TimeUnit
  ): Maybe[Long] =
    Try(socket.write(src)).toEither
