package io.rim99.nio4s

import io.rim99.nio4s.{Maybe, TcpListener, TcpConnection, Events}

import java.nio.channels.{SelectionKey, Selector, ServerSocketChannel}
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

class JvmPoller(val selector: Selector = Selector.open()) extends Poller:
  // multiple selectors with threads in this class

  override def addListener(listener: TcpListener): Unit =
    listener.asInstanceOf[JvmTcpListener]
      .socket
      .getChannel
      .register(selector, SelectionKey.OP_ACCEPT, listener)

  override def poll(): Events =
    selector.select
    val events = selector
      .selectedKeys
      .asScala
      .toList
      .foldLeft[Events](List.empty, List.empty, List.empty, List.empty) {
        case ((a, b, c ,d), key) =>
          (
            Option.when(key.isAcceptable)(new JvmAcceptableEvent(key)).toList ::: a,
            Option.when(key.isWritable)(new JvmWritableEvent(key)).toList ::: b,
            Option.when(key.isReadable)(new JvmReadableEvent(key)).toList ::: c,
            Option.when(key.isConnectable)(new JvmConnectableEvent(key)).toList ::: d,
          )
      }
    selector.selectedKeys().clear()
    events







