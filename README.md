# Scala-Nio

The Scala-Nio is a high performance non-blocking network library for Scala, on both JVM and Native platform. The core functionality is inspired by Nginx `event` mechanism.

The NIO service on JVM is built upon `java.nio.channels.Selector`.

Features:
* One event loop per thread
* NIO on both JVM and *Native* (TODO)

## TODO:
- Timer event
- Native Platform support

## Example

Please check the `Example.scala` under `nio-example-mock-http` package

## LICENSE

MIT
