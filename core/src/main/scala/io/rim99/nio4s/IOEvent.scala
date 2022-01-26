package io.rim99.nio4s

import io.rim99.nio4s.internal.TcpListener

import scala.util.{Failure, Success}

trait ReadableEvent:
  def getTcpChannel: TcpChannel

trait WritableEvent

trait AcceptableEvent:
  def getListener: TcpListener

trait ConnectableEvent

type Events = (
  List[AcceptableEvent],
  List[WritableEvent],
  List[ReadableEvent],
  List[ConnectableEvent]
)

trait EventProcessor:

  final def process(events: Events): Unit =
    processAcceptableEvents(events._1)
    processWritableEvents(events._2)
    processReadableEvents(events._3)
    processConnectableEvents(events._4)

  protected def processAcceptableEvents(a: List[AcceptableEvent]): Unit =
    a.foreach {
      // this is server-style. For client it seems need to be prepared for writing ???
      _.getListener.accept match {
        case Success(inbound) =>
          inbound.prepareForReading()
        case Failure(ex) =>
          // TODO: graceful shutdown
          Logger.trace(ex.toString)
      }

    }

  protected def processConnectableEvents(a: List[ConnectableEvent]): Unit = ()
  protected def processWritableEvents(a: List[WritableEvent]): Unit = ()

  protected def processReadableEvents(a: List[ReadableEvent]): Unit =
    a.foreach { 
      _.getTcpChannel.handleInput() 
    }
