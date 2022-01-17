## nio4s

"nio4s", is short for "Network I/O For Scala".

This is a network I/O runtime library designed for Scala community, both JVM and Native. It only depends on infrastructures provided by languages and operating systems.

### Roadmap

The first step is to implement on **JVM** with minimal usage of Java's `selector` system.

The second step is to implement on **Native** with support for `epoll` and `select`. `kqueue` is also considered, but not the top priority.

The next step is to implement on **Native** with support for `io_uring`. 

### Usage

This is a normal sbt project. You can compile code with `sbt compile`, run it with `sbt run`, and `sbt console` will start a Scala 3 REPL.

For more information on the sbt-dotty plugin, see the
[scala3-example-project](https://github.com/scala/scala3-example-project/blob/main/README.md).
