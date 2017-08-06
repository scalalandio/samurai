package samurai

import org.scalatest.{MustMatchers, WordSpec}

trait Hello {
  def sayHello(): String
}

trait Show[T] {
  def show(x: T): String
}

trait Eq[T] {
  def eq(x1: T, x2: T): Boolean
}

trait Id[T] {
  def id(x: T): T
}

class SamuraiSpec extends WordSpec with MustMatchers {

  "Samurai" must {

    "expand simple 0-arg Show SAM-like implementation" when {

      "declared as val" in {
        @sam val hello: Hello = () => "Hello1"
        hello.sayHello mustBe "Hello1"
      }

      "declared as def" in {
        @sam def hello2: Hello = () => "Hello2"
        hello2.sayHello mustBe "Hello2"
      }

      "declared as var" in {
        @sam var hello3: Hello = () => "Hello3"
        hello3.sayHello mustBe "Hello3"
      }

      "declared as lazy val" in {
        @sam lazy val hello4: Hello = () => "Hello4"
        hello4.sayHello mustBe "Hello4"
      }

      "declared as implicit val" in {
        @sam implicit val hello: Hello = () => "Hello5"
        implicitly[Hello].sayHello mustBe "Hello5"
      }

      "declared as implicit def" in {
        @sam implicit def hello2: Hello = () => "Hello6"
        implicitly[Hello].sayHello mustBe "Hello6"
      }

      "declared as implicit var" in {
        @sam implicit var hello3: Hello = () => "Hello7"
        implicitly[Hello].sayHello mustBe "Hello7"
      }

      "declared as implicit lazy val" in {
        @sam implicit lazy val hello4: Hello = () => "Hello8"
        implicitly[Hello].sayHello mustBe "Hello8"
      }
    }

    "expand simple 1-arg Show SAM-like implementation" when {

      "declared as val" in {
        @sam val intShow: Show[Int] = (x: Int) => x.toString
        intShow.show(5) mustBe "5"
      }

      "declared as def" in {
        @sam def intShow: Show[Int] = (x: Int) => x.toString
        intShow.show(5) mustBe "5"
      }

      "declared as var" in {
        @sam var intShow: Show[Int] = (x: Int) => x.toString
        intShow.show(5) mustBe "5"
      }

      "declared as lazy val" in {
        @sam lazy val intShow: Show[Int] = (x: Int) => x.toString
        intShow.show(5) mustBe "5"
      }

      "declared as implicit val" in {
        @sam implicit val intShow: Show[Int] = (x: Int) => x.toString
        implicitly[Show[Int]].show(5) mustBe "5"
      }

      "declared as implicit def" in {
        @sam implicit def intShow: Show[Int] = (x: Int) => x.toString
        implicitly[Show[Int]].show(5) mustBe "5"
      }

      "declared as implicit var" in {
        @sam implicit var intShow: Show[Int] = (x: Int) => x.toString
        implicitly[Show[Int]].show(5) mustBe "5"
      }

      "declared as implicit lazy val" in {
        @sam implicit lazy val intShow: Show[Int] = (x: Int) => x.toString
        implicitly[Show[Int]].show(5) mustBe "5"
      }
    }

    "expand simple 2-arg Show SAM-like implementation" when {

      "declared as val" in {
        @sam val intEq: Eq[Int] = (x1: Int, x2: Int) => x1 == x2
        intEq.eq(5, 5) mustBe true
      }

      "declared as def" in {
        @sam def intEq: Eq[Int] = (x1: Int, x2: Int) => x1 == x2
        intEq.eq(5, 5) mustBe true
      }

      "declared as var" in {
        @sam var intEq: Eq[Int] = (x1: Int, x2: Int) => x1 == x2
        intEq.eq(5, 5) mustBe true
      }

      "declared as lazy val" in {
        @sam lazy val intEq: Eq[Int] = (x1: Int, x2: Int) => x1 == x2
        intEq.eq(5, 5) mustBe true
      }

      "declared as implicit val" in {
        @sam implicit val intEq: Eq[Int] = (x1: Int, x2: Int) => x1 == x2
        implicitly[Eq[Int]].eq(5, 5) mustBe true
      }

      "declared as implicit def" in {
        @sam implicit def intEq: Eq[Int] = (x1: Int, x2: Int) => x1 == x2
        implicitly[Eq[Int]].eq(5, 5) mustBe true
      }

      "declared as implicit var" in {
        @sam implicit var intEq: Eq[Int] = (x1: Int, x2: Int) => x1 == x2
        implicitly[Eq[Int]].eq(5, 5) mustBe true
      }

      "declared as implicit lazy val" in {
        @sam implicit lazy val intEq: Eq[Int] = (x1: Int, x2: Int) => x1 == x2
        implicitly[Eq[Int]].eq(5, 5) mustBe true
      }
    }

    "expand SAM-like definitions with type parameters and implicit parameters" in {

      @sam implicit val intShow: Show[Int] = (x: Int) => x.toString
      @sam implicit val intStr: Show[String] = (x: String) => x

      @sam implicit def tupleInst[A: Show, B: Show]: Show[(A, B)] =
        (p: (A, B)) => s"(${implicitly[Show[A]].show(p._1)}, ${implicitly[Show[B]].show(p._2)})"

      implicitly[Show[(Int, String)]].show((10, "abc")) mustBe "(10, abc)"
    }

    "expand SAM-like definition when type parameter appears in the return type" in {

      @sam val idInt: Id[Int] = (x: Int) => x

      idInt.id(5) mustBe 5
    }

    "produce compilation error" when {

      "no abstract method" in {

        trait FullyDefined {
          def abc(s1: String, s2: String): String = s1
        }

        assertTypeError {
          """
             @sam val fd: FullyDefined = (s1: String, s2: String) => s1 + s2
          """
        }
      }

      "more than one abstract method" in {

        trait TwoAbstractMethods {
          def m1(x: String): String
          def m2(x: Int): Int
        }

        assertTypeError {
          """
             @sam val tam: TwoAbstractMethods = (x: String) => x
          """
        }
      }

      "not provided a function" in {
        assertTypeError {
          """
             @sam val intShow: Show[Int] = new Show[Int] {
               def show(x: Int): String = x.toString
             }
          """
        }
      }
    }
  }

}
