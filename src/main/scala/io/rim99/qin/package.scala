package io.rim99

package object qin:
  type Maybe[A] = Either[Throwable, A]
