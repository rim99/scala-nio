package io.rim99.nio4s.internal

import io.rim99.nio4s.{NetworkChannel, ProtocolFactory, TcpContext}
import io.rim99.nio4s.ProtocolFactory

import java.net.InetAddress
import scala.util.Try

trait TcpListener extends NetworkChannel:

  val protocolFactory: ProtocolFactory

  def getLocalAddress: Option[InetAddress]

  def getLocalPort: Option[Int]

  def doAccept(): Try[TcpConnection]

  final def accept: Try[TcpContext] = doAccept().map {
    new TcpContext(_, protocolFactory.spawn)
  }
