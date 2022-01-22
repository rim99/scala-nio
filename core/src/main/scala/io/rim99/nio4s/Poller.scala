package io.rim99.nio4s

trait Poller:
  def addListener(serverSocketChannel: TcpListener): Unit
  def poll(): Events

