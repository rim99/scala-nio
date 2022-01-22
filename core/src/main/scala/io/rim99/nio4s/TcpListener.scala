package io.rim99.nio4s

import java.net.{InetAddress, ServerSocket, SocketOption}
import scala.concurrent.Future
import scala.util.Try
import scala.jdk.CollectionConverters.*

trait TcpListener extends NetworkChannel:

  def getLocalAddress: Option[InetAddress]

  def getLocalPort: Option[Int]

  def accept: Future[Maybe[TcpConnection]]

