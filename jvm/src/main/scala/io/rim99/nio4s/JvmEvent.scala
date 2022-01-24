package io.rim99.nio4s

import java.nio.channels.{SelectionKey, Selector}

trait HasKey:
  val k: SelectionKey
  def getTcpConnection: TcpConnection =
    k.attachment().asInstanceOf[JvmTcpConnection]

case class JvmReadableEvent(key: SelectionKey) extends ReadableEvent, HasKey:
  override val k: SelectionKey = key

case class JvmWritableEvent(key: SelectionKey) extends WritableEvent, HasKey:
  override val k: SelectionKey = key
  
case class JvmAcceptableEvent(
  key: SelectionKey,
  selector: Selector
) extends AcceptableEvent:

  override def getListener: TcpListener =
    key.attachment().asInstanceOf[JvmTcpListener]

case class JvmConnectableEvent(key: SelectionKey) extends ConnectableEvent, HasKey:
  override val k: SelectionKey = key
