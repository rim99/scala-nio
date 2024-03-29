package io.rim99.nio4s

import io.rim99.nio4s.internal.JvmTcpListener
import io.rim99.nio4s.Events
import io.rim99.nio4s.internal.JvmTcpConnection

import java.nio.channels.{SelectionKey, Selector}
import java.util.concurrent.locks.ReentrantLock
import scala.jdk.CollectionConverters.*
import scala.util.{Failure, Success, Try}

class JvmConnectionManager(override val workMode: WorkMode)
    extends ConnectionManager:

  override def newWorker: Worker = new JvmWorker()

  override def addListener(
    port: Int,
    factory: ProtocolFactory,
    transport: Transport = Transport.TCP
  ): Unit =
    new JvmTcpListener(port, this, factory)
      .registerOn(acceptor.asInstanceOf[JvmWorker].selector)

  override def setSignalHandler(): Unit =
    val shutdownCallback = new Runnable():
      override def run(): Unit = JvmConnectionManager.this.close()
    Runtime.getRuntime.addShutdownHook(new Thread(shutdownCallback))

  override def waitForever(): Unit =
    val l = new ReentrantLock()
    l.lock()
    val cond = l.newCondition()
    Logger.trace("wait on cond")
    cond.await()

class JvmWorker extends Worker:
  override val bufferPool: ByteBufferPool = new ByteBufferPool
  val selector: Selector = Selector.open()

  override def getLoad: Int = selector.keys().size()

  override def close(): Unit =
    Logger.trace(s"Worker ${this} closed with load: ${getLoad}")

  override def poll(): Events =
    selector.select
    Logger.trace(s"Total key size: ${selector.keys().size()}")
    val events = selector.selectedKeys.asScala.toList
      .foldLeft[Events](List.empty, List.empty, List.empty, List.empty) {
        case ((a, b, c, d), key) =>
          (
            Option
              .when(key.isAcceptable) {
                val listener = key.attachment().asInstanceOf[JvmTcpListener]
                AcceptableEvent(listener)
              }
              .toList ::: a,
            Option
              .when(key.isWritable) {
                val ctx = key.attachment().asInstanceOf[TcpContext]
                WritableEvent(ctx)
              }
              .toList ::: b,
            Option
              .when(key.isReadable) {
                val ctx = key.attachment().asInstanceOf[TcpContext]
                ReadableEvent(ctx)
              }
              .toList ::: c,
            Option
              .when(key.isConnectable) {
                val ctx = key.attachment().asInstanceOf[TcpContext]
                ConnectableEvent(ctx)
              }
              .toList ::: d
          )
      }
    selector.selectedKeys().clear()
    Logger.trace(s"Total key size: ${selector.keys().size()}")
    Logger.trace(
      s"Events: ${events._1.size}, ${events._2.size}, ${events._3.size}, ${events._4.size}"
    )
    events
