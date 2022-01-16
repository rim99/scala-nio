package io.rim99.qin

import java.net.{Socket, InetAddress, SocketOption}
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit
import scala.jdk.CollectionConverters.*
import scala.concurrent.Future
import scala.util.Try

trait SocketChannel extends NetworkChannel:

  val socket: Socket

  def getLocalAddress: Option[InetAddress] =
    Try(Option(socket.getLocalAddress)).toOption.flatten

  def getRemoteAddress: Option[InetAddress] =
    Try(Option(socket.getInetAddress)).toOption.flatten
    
  def getLocalPort: Option[Int] =
    val port = socket.getLocalPort
    Option.when(port != -1)(port)

  def getRemotePort: Option[Int] =
    val port = socket.getPort
    Option.when(port != -1)(port)
    
  override def getOption[T](name: SocketOption[T]): Try[T] =
    Try(socket.getOption(name))

  override def supportedOptions: Set[SocketOption[?]] =
    socket.supportedOptions().asScala.toSet

  override def setOption[T](
    name: SocketOption[T],
    value: T
  ): Try[Unit] = Try(socket.setOption(name, value))

  def shutdownInput: Try[Unit]

  def shutdownOutput: Try[Unit]

  def read[A](
    dst: ByteBuffer,
    timeout: Long = 0L,
    unit: TimeUnit = TimeUnit.MILLISECONDS
  ): Future[Maybe[Integer]]

  def readAll[A](
    dst: Array[ByteBuffer],
    offset: Int,
    length: Int,
    timeout: Long = 0L,
    unit: TimeUnit = TimeUnit.MILLISECONDS
  ): Future[Maybe[Long]]

  def write[A](
    src: ByteBuffer,
    timeout: Long = 0L,
    unit: TimeUnit = TimeUnit.MILLISECONDS
  ): Future[Maybe[Integer]]

  def writeAll[A](
    src: Array[ByteBuffer],
    offset: Int,
    length: Int,
    timeout: Long = 0L,
    unit: TimeUnit = TimeUnit.MILLISECONDS
  ): Future[Maybe[Integer]]
