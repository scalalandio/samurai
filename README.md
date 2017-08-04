# Samurai

[![Build Status](https://travis-ci.org/scalalandio/samurai.svg?branch=master)](https://travis-ci.org/scalalandio/samurai)
[![Maven Central](https://img.shields.io/maven-central/v/io.scalaland/samurai_2.11.svg)](http://search.maven.org/#search%7Cga%7C1%7Csamurai)
[![License](http://img.shields.io/:license-Apache%202-green.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)

SAM-like macro annotation for Scala

Scala 2.12 introduced simplified syntax for defining instances of
classes/traits that contain single abstract method. Considering
following interface:

```scala
trait Show[T] {
  def show(x: T): String
}
``` 

Instead of

```scala
val intShow: Show[Int] = new Show[Int] {
  def show(x: Int): String = x.toString
}
```

we can simply write

```scala
val intShow: Show[Int] = (x: Int) => x.toString
```

Unfortunately, in Scala 2.11 such a syntax is not possible and for
every type we have to either define separate helper function or
live with this boilerplate.

Samurai library defines macro annotation called `@sam` that
brings this nice syntax also Scala 2.11.

## Setup

You have to add following lines to your `build.sbt`:

```scala
libraryDependencies += "io.scalaland" %% "samurai" % "1.0"

resolvers += Resolver.sonatypeRepo("releases")
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
```

## Usage

Just prepend your `val`/`def` definitions with `@sam` annotation.

```scala
import samurai._
@sam val intShow: Show[Int] = (x: Int) => x.toString
```

This will expand in compile-time to the following piece of code:

```scala
val intShow: Show[Int] = new Show[Int] {
  def show(x: Int): String = x.toString
}
```

### Advanced example

```scala
import samurai._
@sam implicit val intShow: Show[Int] = (x: Int) => x.toString
@sam implicit val intStr: Show[String] = (x: String) => x

@sam implicit def tupleInst[A: Show, B: Show]: Show[(A, B)] =
  (p: (A, B)) => s"(${implicitly[Show[A]].show(p._1)}, ${implicitly[Show[B]].show(p._2)})"
```

Last definition will be expanded to something similar to:

```scala
implicit def tupleInst[A, B](implicit s1: Show[A], s2: Show[B]): Show[(A, B)] = 
  new Show[(A, B)] {
    def show(s: (A, B)): String = s"(${s1.show(p._1)}, ${s2.show(p._2)})"
  }
```

### Quirks

I observed that this approach might not work for every local type definition,
because of the quirks of scala reflection API, so recommended approach
is to define all your traits/abstract classes that you want to instantiate
with `@sam` annotation as top-level ones.

## Cross compilation

The library is cross-compiled on 2.11 and 2.12. On Scala 2.11 it performs
real expansion described above, while on 2.12 it relies on native
support provided by compiler, so it actually doesn't transform the
original definition in any way.
