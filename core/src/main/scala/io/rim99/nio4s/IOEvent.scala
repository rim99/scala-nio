package io.rim99.nio4s

import io.rim99.nio4s.internal.TcpListener

import scala.util.{Failure, Success, Try}

case class AcceptableEvent(listener: TcpListener):
  def process: Try[TcpContext] = listener.accept

case class ReadableEvent(ctx: TcpContext):
  def process(): Unit = ctx.handleInput()

trait WritableEvent:
  def process(): Unit

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
      _.process match {
        case Success(inbound) =>
          inbound.prepareForReading()
        case Failure(ex) =>
          // TODO: graceful shutdown
          Logger.trace(ex.toString)
      }
    }

  protected def processWritableEvents(a: List[WritableEvent]): Unit = ()

  protected def processReadableEvents(a: List[ReadableEvent]): Unit =
    a.foreach(_.process())

  protected def processConnectableEvents(a: List[ConnectableEvent]): Unit = ()

