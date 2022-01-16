package io.rim99.qin

import java.net.{InetAddress, ServerSocket, SocketOption}
import scala.concurrent.Future
import scala.util.Try
import scala.jdk.CollectionConverters.*

trait ServerSocketChannel extends NetworkChannel:

  val socket: ServerSocket

  def getLocalAddress: Option[InetAddress] =
    Try(Option(socket.getInetAddress)).toOption.flatten

  def getLocalPort: Option[Int] =
    val port = socket.getLocalPort
    Option.when(port != -1)(port)

  override def getOption[T](name: SocketOption[T]): Try[T] = 
    Try(socket.getOption(name))

  override def supportedOptions: Set[SocketOption[?]] =
    socket.supportedOptions().asScala.toSet

  override def setOption[T](
    name: SocketOption[T],
    value: T
  ): Try[Unit] = Try(socket.setOption(name, value))

  def accept: Future[SocketChannel]

