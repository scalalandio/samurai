package samurai

import org.scalatest.{MustMatchers, WordSpec}

trait Hello[T] {
  def sayHello(): String
}

trait Show[T] {
  def show(x: T): String
}

trait Eq[T] {
  def eq(x1: T, x2: T): Boolean
}


class SamuraiSpec extends WordSpec with MustMatchers {

  "Samurai" must {


    "expand simple 0-arg Show SAM-like implementation" when {

      "declared as val" in {
        @sam val intHello: Hello[Int] = () => "Hello1"
        intHello.sayHello mustBe "Hello1"
      }

      "declared as def" in {
        @sam def intHello2: Hello[Int] = () => "Hello2"
        intHello2.sayHello mustBe "Hello2"
      }

      "declared as var" in {
        @sam var intHello3: Hello[Int] = () => "Hello3"
        intHello3.sayHello mustBe "Hello3"
      }

      "declared as lazy val" in {
        @sam lazy val intHello4: Hello[Int] = () => "Hello4"
        intHello4.sayHello mustBe "Hello4"
      }

      "declared as implicit val" in {
        @sam implicit val intHello: Hello[Int] = () => "Hello5"
        implicitly[Hello[Int]].sayHello mustBe "Hello5"
      }

      "declared as implicit def" in {
        @sam implicit def intHello2: Hello[Int] = () => "Hello6"
        implicitly[Hello[Int]].sayHello mustBe "Hello6"
      }

      "declared as implicit var" in {
        @sam implicit var intHello3: Hello[Int] = () => "Hello7"
        implicitly[Hello[Int]].sayHello mustBe "Hello7"
      }

      "declared as implicit lazy val" in {
        @sam implicit lazy val intHello4: Hello[Int] = () => "Hello8"
        implicitly[Hello[Int]].sayHello mustBe "Hello8"
      }
    }


    "expand simple 1-arg Show SAM-like implementation" in {


//      val intShow: Show[Int] = sam[Show[Int]]((x: Int) => x.toString)
      @sam val intShow: Show[Int] = (x: Int) => x.toString

      intShow.show(5) mustBe "5"
    }

    "expand simple 2-arg Show SAM-like implementation" in {


//      val intEq = sam[Eq[Int]]((x1: Int, x2: Int) => x1 == x2)
      @sam val intEq: Eq[Int] = (x1: Int, x2: Int) => x1 == x2

      intEq.eq(5, 5) mustBe true
    }

    "expand SAM-like definitions with type parameters and implicit parameters" in {

      @sam implicit val intShow: Show[Int] = (x: Int) => x.toString
      @sam implicit val intStr: Show[String] = (x: String) => x

      @sam implicit def tupleInst[A: Show, B: Show]: Show[(A, B)] =
        (p: (A, B)) => s"(${implicitly[Show[A]].show(p._1)}, ${implicitly[Show[B]].show(p._2)})"

      implicitly[Show[(Int, String)]].show((10, "abc")) mustBe "(10, abc)"
    }
  }

}
