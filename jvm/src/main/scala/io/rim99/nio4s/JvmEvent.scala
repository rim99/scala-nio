package io.rim99.nio4s

import java.nio.channels.SelectionKey

case class JvmReadableEvent(key: SelectionKey) extends ReadableEvent
case class JvmWritableEvent(key: SelectionKey) extends WritableEvent
case class JvmAcceptableEvent(key: SelectionKey, poller: Poller) extends AcceptableEvent
case class JvmConnectableEvent(key: SelectionKey) extends ConnectableEvent
