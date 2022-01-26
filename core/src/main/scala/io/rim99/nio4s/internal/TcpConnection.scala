package io.rim99.nio4s.internal

import io.rim99.nio4s.{IOError, NetworkChannel, TcpChannel}

import java.net.InetAddress
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit
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
  ): Either[IOError, Int]

  def scatterRead(
    dst: Array[ByteBuffer],
    offset: Int,
    length: Int,
    timeout: Long = NEVER,
    unit: TimeUnit = TimeUnit.MILLISECONDS
  ): Either[IOError, Long]

  def write(
    src: ByteBuffer,
    timeout: Long = NEVER,
    unit: TimeUnit = TimeUnit.MILLISECONDS
  ): Either[IOError, Int]

  def gatherWrite(
    src: Array[ByteBuffer],
    offset: Int,
    length: Int,
    timeout: Long = NEVER,
    unit: TimeUnit = TimeUnit.MILLISECONDS
  ): Either[IOError, Long]

  def prepareForReading(c: TcpChannel): Unit
