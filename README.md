# APILET

The Apilet is a high performance non-blocking embedded HTTP server library for Scala, on both JVM and Native platform. It is mainly for internal network usage. If apilet-based services are required to exposed to external networks, an HTTP gateway, like Nginx or Apache Server, is always recommended. 

Features:
* One event loop per thread
* NIO on both JVM and Native (TODO)
* HTTP/1.1 (TODO)
* HTTP2 (TODO)
* SpringBoot-like Router (TODO)

## Component Stack

Apilet is loosely coupled by a few components, including:
* `apilet-nio`
* `apilet-http`
* `apilet-router`

The lower one depends on all its upper components. So for library developers, perhaps you only need `apilet-nio` and/or `apilet-http`.

## NIO 

"apilet-nio", is a network I/O runtime library designed for Scala community, both JVM and Native. The core functionality is inspired by Nginx `event` mechanism.

The NIO service on JVM is built upon `java.nio.channels.Selector`. 

> On Native, it is built upon `libev`.
> TBD

## HTTP protocol

TBD

## Router

TBD
