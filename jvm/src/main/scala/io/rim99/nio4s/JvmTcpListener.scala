package io.rim99.nio4s

import io.rim99.nio4s.{TcpConnection, TcpListener}

import java.net.{InetAddress, InetSocketAddress, ServerSocket, SocketOption}
import java.nio.channels.{ServerSocketChannel, SocketChannel}
import scala.concurrent.Future
import scala.util.Try
import scala.jdk.CollectionConverters.*

class JvmTcpListener(
  val port: Int,
  val poller: JvmPoller
) extends TcpListener:

  val socket: ServerSocketChannel =
    val s = ServerSocketChannel.open
    s.bind(new InetSocketAddress(port))
    s.configureBlocking(false)
    s

  override def close(): Unit =
    // TODO: graceful shutdown
    socket.close()

  override def isOpen: Boolean = !socket.socket().isClosed

  def getLocalAddress: Option[InetAddress] =
    Try(Option(socket.socket().getInetAddress)).toOption.flatten

  def getLocalPort: Option[Int] =
    val port = socket.socket().getLocalPort
    Option.when(port != -1)(port)

  override def getOption[T](name: SocketOption[T]): Try[T] =
    Try(socket.socket().getOption(name))

  override def supportedOptions: Set[SocketOption[?]] =
    socket.socket().supportedOptions().asScala.toSet

  override def setOption[T](
    name: SocketOption[T],
    value: T
  ): Try[Unit] = Try(socket.socket().setOption(name, value))

  override def accept: Try[TcpConnection] =
    Try(socket.accept().configureBlocking(false))
      .map(_.asInstanceOf[SocketChannel])
      .map(new JvmTcpConnection(_, poller.selector)) // TODO: pick thread

  def onAccept(): Unit = () // configure accepted sock
