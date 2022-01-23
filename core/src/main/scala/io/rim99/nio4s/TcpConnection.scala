package io.rim99.nio4s

import java.net.{Socket, InetAddress, SocketOption}
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit
import scala.concurrent.Future
import scala.util.Try

trait TcpConnection extends NetworkChannel:
  val NEVER = -1L

  def getLocalAddress: Option[InetAddress]

  def getRemoteAddress: Option[InetAddress]

  def getLocalPort: Option[Int]

  def getRemotePort: Option[Int]

  def shutdownInput: Try[Unit]

  def shutdownOutput: Try[Unit]

  def read(
    dst: ByteBuffer,
    timeout: Long = NEVER,
    unit: TimeUnit = TimeUnit.MILLISECONDS
  ): Maybe[Int]

  def readAll(
    dst: Array[ByteBuffer],
    offset: Int,
    length: Int,
    timeout: Long = NEVER,
    unit: TimeUnit = TimeUnit.MILLISECONDS
  ): Maybe[Long]

  def write(
    src: ByteBuffer,
    timeout: Long = NEVER,
    unit: TimeUnit = TimeUnit.MILLISECONDS
  ): Maybe[Int]

  def writeAll(
    src: Array[ByteBuffer],
    offset: Int,
    length: Int,
    timeout: Long = NEVER,
    unit: TimeUnit = TimeUnit.MILLISECONDS
  ): Maybe[Long]
