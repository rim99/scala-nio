package io.rim99.nio4s

import io.rim99.nio4s.internal.{TcpConnection, TcpListener}

import java.util.concurrent.locks.ReentrantLock
import scala.util.{Failure, Success, Try}

trait ConnectionManager:
  lazy val acceptor: Worker = workers(0)
  lazy val poller: List[Worker] = workers.toList.tail
  val workers: Array[Worker]

  def addListener(
    port: Int,
    factory: ProtocolFactory,
    transport: Transport = Transport.TCP
  ): Unit

  def runAsync(): Unit = workers.foreach(_.runAsync())

  def await(): Unit

  def close(): Unit

  def pickWorker: Worker =
    val cnt = workers.length
    if cnt == 1 then acceptor // only one worker, just return it
    else poller.minBy(_.getLoad)

trait Worker extends Runnable:

  private val t: Thread =
    val t = new Thread(Worker.this)
    t.setDaemon(false)
    t

  override def run(): Unit =
    while true do
      // TODO: schedule with timer
      val events = poll()
      events.process()

  def runAsync(): Unit = t.start()

  def poll(): Events

  def close(): Unit

  def getLoad: Int

enum Transport:
  case TCP