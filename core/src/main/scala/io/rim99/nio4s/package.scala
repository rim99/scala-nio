package io.rim99

package object nio4s:
  type Maybe[A] = Either[Throwable, A]
