package io.rim99.nio4s

import java.nio.channels.ServerSocketChannel

trait ReadableEvent
trait WritableEvent
trait AcceptableEvent
trait ConnectableEvent
type Events = (List[AcceptableEvent], List[WritableEvent], List[ReadableEvent], List[ConnectableEvent])

trait EventProcessor:
  final def process(events: Events): Unit =
    processAcceptableEvents(events._1)
    processWritableEvents(events._2)
    processReadableEvents(events._3)
    processConnectableEvents(events._4)

  protected def processAcceptableEvents(a: List[AcceptableEvent]): Unit = ()
  protected def processConnectableEvents(a: List[ConnectableEvent]): Unit = ()
  protected def processWritableEvents(a: List[WritableEvent]): Unit = ()
  protected def processReadableEvents(a: List[ReadableEvent]): Unit = ()