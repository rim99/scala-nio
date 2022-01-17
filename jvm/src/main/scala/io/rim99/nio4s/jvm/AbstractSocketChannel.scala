package io.rim99.nio4s.jvm

import io.rim99.nio4s.SocketChannel

import java.net.{InetAddress, Socket, SocketOption}
import scala.util.Try
import scala.jdk.CollectionConverters.*

abstract class AbstractSocketChannel extends SocketChannel:
  protected val socket: Socket

  override def getLocalAddress: Option[InetAddress] =
    Try(Option(socket.getLocalAddress)).toOption.flatten

  override def getRemoteAddress: Option[InetAddress] =
    Try(Option(socket.getInetAddress)).toOption.flatten

  override def getLocalPort: Option[Int] =
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
