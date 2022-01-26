package io.rim99.nio4s.internal

import io.rim99.nio4s.{NetworkChannel, TcpChannel}

import java.net.InetAddress
import scala.util.Try

trait TcpListener extends NetworkChannel:

  def getLocalAddress: Option[InetAddress]

  def getLocalPort: Option[Int]

  def accept: Try[TcpChannel]
