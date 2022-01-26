package io.rim99.nio4s

import io.rim99.nio4s.internal.{JvmTcpListener, TcpListener}

import java.nio.channels.{SelectionKey, Selector}

case class JvmReadableEvent(key: SelectionKey) extends ReadableEvent:
  override def getTcpChannel: TcpChannel =
    key.attachment().asInstanceOf[TcpChannel]

case class JvmWritableEvent(key: SelectionKey) extends WritableEvent

case class JvmAcceptableEvent(
  key: SelectionKey,
  selector: Selector
) extends AcceptableEvent:

  override def getListener: TcpListener =
    key.attachment().asInstanceOf[JvmTcpListener]

case class JvmConnectableEvent(key: SelectionKey) extends ConnectableEvent
