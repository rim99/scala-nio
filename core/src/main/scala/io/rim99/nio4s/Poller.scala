package io.rim99.nio4s

import io.rim99.nio4s.internal.{TcpConnection, TcpListener}

trait Worker

trait Poller:
  def addListener(l: TcpListener): Unit
  def addForReading(c: TcpConnection): Unit
  def pickWorker: Worker
  def poll(): Events

