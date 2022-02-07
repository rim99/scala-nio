package io.apilet.nio4s.internal

import io.apilet.nio4s.{IOError, IOErrors, JvmWorker, TcpContext, Worker}
import io.apilet.nio4s.TcpContext

import java.net.{InetAddress, SocketOption}
import java.nio.ByteBuffer
import java.nio.channels.{SelectionKey, Selector, SocketChannel}
import java.util.concurrent.TimeUnit
import scala.concurrent.Future
import scala.jdk.CollectionConverters.*
import scala.util.{Failure, Success, Try}

class JvmTcpConnection(
  val socket: SocketChannel,
  override val worker: Worker
) extends TcpConnection:

  override def prepareForReading(attachment: TcpContext): Unit =
    val selector = worker.asInstanceOf[JvmWorker].selector
    socket.register(selector, SelectionKey.OP_READ, attachment)
    selector.wakeup()
    ()

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
    val selector = worker.asInstanceOf[JvmWorker].selector
    socket.keyFor(selector).cancel()
    socket.close()

  override def isOpen: Boolean = !socket.socket().isClosed

  override def shutdownInput: Try[Unit] = Try(socket.shutdownInput())

  override def shutdownOutput: Try[Unit] = Try(socket.shutdownOutput())

  override def read(
    dst: ByteBuffer,
    timeout: Long,
    unit: TimeUnit
  ): Either[IOError, Int] =
    // TODO: generated read event and create timer event
    Try(socket.read(dst)).wrap

  override def scatterRead(
    dst: Array[ByteBuffer],
    offset: Int,
    length: Int,
    timeout: Long,
    unit: TimeUnit
  ): Either[IOError, Long] =
    Try(socket.read(dst)).toEither.left
      .map(_ => IOErrors.EOF)

  override def write(
    src: ByteBuffer,
    timeout: Long,
    unit: TimeUnit
  ): Either[IOError, Int] =
    Try(socket.write(src)).wrap

  override def gatherWrite(
    src: Array[ByteBuffer],
    offset: Int,
    length: Int,
    timeout: Long,
    unit: TimeUnit
  ): Either[IOError, Long] =
    Try(socket.write(src)).toEither.left
      .map(_ => IOErrors.EOF)

  extension (ret: Try[Int])

    @inline
    final def wrap: Either[IOError, Int] =
      ret match
        case Success(i: Int) if i >= 0 => Right(i)
        case Success(-1) => Left(IOErrors.EOF)
        case Success(other) =>
          Left(IOErrors.Exception(s"Cannot handle value: $other"))
        case Failure(ex) => Left(IOErrors.Exception(ex.getMessage))
