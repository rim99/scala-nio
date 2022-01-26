package io.rim99.nio4s

import io.rim99.nio4s.internal.TcpListener

trait NioServer:
  val poller: Poller
  

trait TcpService:
  val listeners: List[TcpListener]

trait UdpService


