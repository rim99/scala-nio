package io.rim99.nio4s

import io.rim99.nio4s.Events
import io.rim99.nio4s.internal.{JvmTcpConnection, JvmTcpListener, TcpConnection, TcpListener}

import java.nio.channels.{SelectionKey, Selector}
import scala.jdk.CollectionConverters.*

class JvmPoller(val selector: Selector = Selector.open()) extends Poller:
  val w = new JvmWorker(selector)

  override def pickWorker: Worker = w
  
  override def addListener(listener: TcpListener): Unit =
    listener
      .asInstanceOf[JvmTcpListener]
      .socket
      .register(selector, SelectionKey.OP_ACCEPT, listener)

  override def addForReading(c: TcpConnection): Unit =
    c.asInstanceOf[JvmTcpConnection]
      .socket
      .register(selector, SelectionKey.OP_READ, c)

  override def poll(): Events =
    selector.select
    Logger.trace(s"Total key size: ${selector.keys().size()}")
    val events = selector.selectedKeys.asScala.toList
      .foldLeft[Events](List.empty, List.empty, List.empty, List.empty) {
        case ((a, b, c, d), key) =>
          (
            Option
              .when(key.isAcceptable)(JvmAcceptableEvent(key, selector))
              .toList ::: a,
            Option
              .when(key.isWritable)(JvmWritableEvent(key))
              .toList ::: b,
            Option
              .when(key.isReadable)(JvmReadableEvent(key))
              .toList ::: c,
            Option
              .when(key.isConnectable)(JvmConnectableEvent(key))
              .toList ::: d
          )
      }
    selector.selectedKeys().clear()
    Logger.trace(s"Total key size: ${selector.keys().size()}")
    Logger.trace(
      s"Events: ${events._1.size}, ${events._2.size}, ${events._3.size}, ${events._4.size}"
    )
    events

class JvmWorker(val selector: Selector) extends Worker