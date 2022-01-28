package io.rim99.nio4s

import io.rim99.nio4s.internal.{TcpConnection, TcpListener}

import java.util.concurrent.locks.ReentrantLock
import scala.util.{Failure, Success, Try}

trait Worker extends Runnable:
  val ev = new EventProcessor()

  val t: Thread =
    val t = new Thread(Worker.this)
    t.setDaemon(false)
    t

  override def run(): Unit =
    while true do
      // TODO: schedule with timer
      val events = poll()
      ev.process(events)

  def runAsync(): Unit = t.start()

  def poll(): Events
  
  def close(): Unit

  def getLoad: Int

enum Transport:
  case TCP

trait Poller:
  val workers: Array[Worker]

  lazy val listenWorker: Worker = workers(0)

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
    if cnt == 1 then listenWorker // only one worker, just return it
    else
      // try to even the loads
      workers.toList.tail
        .map { w =>
          (w.getLoad, w)
        }
        .minBy { case (load, _) =>
          load
        }
        ._2
