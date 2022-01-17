package io.rim99.nio4s

import java.net.{Socket, InetAddress, SocketOption}
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit
import scala.concurrent.Future
import scala.util.Try

trait SocketChannel extends NetworkChannel:

  def getLocalAddress: Option[InetAddress]

  def getRemoteAddress: Option[InetAddress]

  def getLocalPort: Option[Int]

  def getRemotePort: Option[Int]

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
