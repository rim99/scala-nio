package io.rim99.nio4s

import io.rim99.nio4s.Events
import io.rim99.nio4s.internal.{
  JvmTcpConnection,
  JvmTcpListener,
  TcpConnection,
  TcpListener
}

import java.nio.channels.{SelectionKey, Selector}
import java.util.concurrent.locks.ReentrantLock
import scala.jdk.CollectionConverters.*
import scala.util.{Failure, Success, Try}

class JvmPoller(worker: Int = 1) extends Poller:

  override val workers: Array[Worker] =
    val cnt = if worker == 1 then worker else worker + 1
    Array.fill(cnt)(new JvmWorker())

  override def close(): Unit =
    workers.foreach(_.close())

  override def addListener(
    port: Int,
    factory: ProtocolFactory,
    transport: Transport = Transport.TCP
  ): Unit =
    new JvmTcpListener(port, this, factory)
      .registerOn(listenWorker.asInstanceOf[JvmWorker].selector)

  def waitForever(): Unit =
    val l = new ReentrantLock()
    l.lock()
    val cond = l.newCondition()
    Logger.trace("wait on cond")
    cond.await()

  override def await(): Unit =
    val shutdownCallback = new Runnable():
      override def run(): Unit = JvmPoller.this.close()
    Runtime.getRuntime.addShutdownHook(new Thread(shutdownCallback))
    runAsync()
    waitForever()

class JvmWorker extends Worker:
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
              .when(key.isWritable)(JvmWritableEvent(key))
              .toList ::: b,
            Option
              .when(key.isReadable) {
                val ctx = key.attachment().asInstanceOf[TcpContext]
                ReadableEvent(ctx)
              }
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
