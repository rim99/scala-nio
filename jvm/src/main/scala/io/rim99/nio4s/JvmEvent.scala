package io.rim99.nio4s

import io.rim99.nio4s.internal.{JvmTcpListener, TcpListener}

import java.nio.channels.{SelectionKey, Selector}

case class JvmWritableEvent(key: SelectionKey) extends WritableEvent:
  override def process(): Unit = ()

case class JvmConnectableEvent(key: SelectionKey) extends ConnectableEvent
