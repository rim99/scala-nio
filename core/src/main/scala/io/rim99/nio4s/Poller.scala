package io.rim99.nio4s

trait Poller:
  def addListener(l: TcpListener): Unit
  def addForReading(c: TcpConnection): Unit
  def poll(): Events

