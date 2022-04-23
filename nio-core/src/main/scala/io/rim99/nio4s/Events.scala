package io.rim99.nio4s

import io.rim99.nio4s.internal.TcpListener

import scala.util.{Failure, Success, Try}

case class AcceptableEvent(listener: TcpListener):

  def process(): Unit = listener.accept match
    case Success(inbound) =>
      inbound.prepare()
    case Failure(ex) =>
      // TODO: remove listener
      Logger.trace(ex.toString)

case class ReadableEvent(ctx: TcpContext):
  def process(): Unit = ctx.handleInput()

case class WritableEvent(ctx: TcpContext):
  def process(): Unit = ctx.handleOutput()

case class ConnectableEvent(ctx: TcpContext):
  def process(): Unit = ()

type Events = (
  List[AcceptableEvent],
  List[WritableEvent],
  List[ReadableEvent],
  List[ConnectableEvent]
)

extension (events: Events)

  final def process(): Unit =
    events._1.foreach(_.process())
    events._2.foreach(_.process())
    events._3.foreach(_.process())
    events._4.foreach(_.process())
