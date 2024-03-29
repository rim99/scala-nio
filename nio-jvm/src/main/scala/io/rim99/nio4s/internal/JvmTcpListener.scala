package io.rim99.nio4s.internal

import io.rim99.nio4s.{JvmConnectionManager, JvmWorker, ProtocolFactory}
import io.rim99.nio4s.*

import java.net.{InetAddress, InetSocketAddress, ServerSocket, SocketOption}
import java.nio.channels.{
  SelectionKey,
  Selector,
  ServerSocketChannel,
  SocketChannel
}
import scala.concurrent.Future
import scala.jdk.CollectionConverters.*
import scala.util.Try

class JvmTcpListener(
  val port: Int,
  val connectionManager: ConnectionManager,
  override val protocolFactory: ProtocolFactory
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

  override def doAccept(): Try[TcpConnection] =
    Try {
      socket
        .accept()
        .configureBlocking(false)
        .asInstanceOf[SocketChannel]
    }.map { sock =>
      val worker = connectionManager.pickWorker
      new JvmTcpConnection(sock, worker)
    }

  def onAccept(): Unit = () // configure accepted sock

  def registerOn(selector: Selector): Unit =
    socket.register(
      selector,
      SelectionKey.OP_ACCEPT,
      this
    )
