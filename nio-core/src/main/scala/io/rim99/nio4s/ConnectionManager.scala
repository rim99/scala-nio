package io.rim99.nio4s

import io.rim99.nio4s.internal.TcpListener

import java.util.concurrent.locks.ReentrantLock
import scala.util.{Failure, Success, Try}

trait ConnectionManager:
  /** Light Workload Mode: To save the CPU/memory resource, only one worker is
    * created, which will handle both accepting connections and processing
    * established connections.
    *
    * Heavy Workload Mode: Multiple workers are created. Pollers are for
    * processing established connections only. To prevent pollers from being
    * interrupted by connection accepting, a dedicated "acceptor" is created.
    */
  val workMode: WorkMode
  lazy val acceptor: Worker = workers(0)
  lazy val poller: Array[Worker] = workers.slice(1, workers.length)

  val workers: Array[Worker] = workMode match
    case WorkModes.Light => Array.fill(1)(newWorker)
    case WorkModes.Heavy(n) => Array.fill(n + 1)(newWorker)

  def newWorker: Worker

  def addListener(
    port: Int,
    factory: ProtocolFactory,
    transport: Transport = Transport.TCP
  ): Unit

  def close(): Unit = workers.foreach(_.close())

  def runAsync(): Unit =
    setSignalHandler()
    workers.foreach(_.runAsync())

  def setSignalHandler(): Unit

  def waitForever(): Unit

  def await(): Unit =
    runAsync()
    waitForever()

  def pickWorker: Worker = workMode match
    case WorkModes.Light => acceptor // only one worker, just return it
    case WorkModes.Heavy(_) => poller.minBy(_.getLoad)

trait Worker extends Runnable:

  val bufferPool: ByteBufferPool

  private val t: Thread =
    val t = new Thread(Worker.this)
    t.setDaemon(false)
    t

  override def run(): Unit =
    while true do
      // TODO: schedule with timer
      poll().process()

  def runAsync(): Unit = t.start()

  def poll(): Events

  def close(): Unit

  def getLoad: Int

enum Transport:
  case TCP

sealed trait WorkMode

object WorkModes:
  case object Light extends WorkMode
  case class Heavy(parallelism: Int) extends WorkMode
