package io.rim99.nio4s

import io.rim99.nio4s.internal.{TcpConnection, TcpListener}

trait Worker

enum Transport:
  case TCP

trait Poller:

  def addListener(
    port: Int,
    factory: ProtocolFactory,
    transport: Transport = Transport.TCP
  ): Unit

  def pickWorker: Worker
  def poll(): Events
