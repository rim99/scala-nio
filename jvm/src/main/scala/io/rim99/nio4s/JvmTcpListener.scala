package io.rim99.nio4s

import io.rim99.nio4s.{Maybe, TcpListener, TcpConnection}

import java.net.{InetAddress, InetSocketAddress, ServerSocket, SocketOption}
import java.nio.channels.ServerSocketChannel as JServerSocketChannel
import scala.concurrent.Future
import scala.util.Try
import scala.jdk.CollectionConverters.*

class JvmTcpListener(port: Int) extends TcpListener:

  val socket: ServerSocket =
    val s = JServerSocketChannel.open
    s.bind(new InetSocketAddress(port))
    s.configureBlocking(false)
    s.socket()

  override def close(): Unit =
    // TODO: graceful shutdown
    socket.close()

  override def isOpen: Boolean = !socket.isClosed

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

  override def accept: Future[Maybe[TcpConnection]] = ???
