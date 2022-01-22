package io.rim99.nio4s

trait NioServer:
  val poller: Poller
  

trait TcpService:
  val listeners: List[TcpListener]

trait UdpService


