package io.apilet.nio4s

import io.apilet.nio4s.internal.TcpListener

trait NioServer:
  val connectionManager: ConnectionManager

trait TcpService:
  val listeners: List[TcpListener]

trait UdpService
