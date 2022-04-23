package io.rim99.nio4s

import java.net.{Socket, SocketAddress, SocketOption}
import java.nio.channels.Channel as JChannel
import scala.jdk.CollectionConverters.*
import scala.util.Try

trait NetworkChannel extends JChannel:

  def getOption[T](name: SocketOption[T]): Try[T]

  def supportedOptions: Set[SocketOption[?]]

  def setOption[T](
    name: SocketOption[T],
    value: T
  ): Try[Unit]
