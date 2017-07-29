package samurai

import org.scalatest.{MustMatchers, WordSpec}

class SamuraiSpec extends WordSpec with MustMatchers {

  "Samurai" must {

    "expand simple 0-arg Show SAM-like implementation" in {

      trait Hello[T] {
        def sayHello(): String
      }

      val intHello = sam[Hello[Int]](() => "Hello")

      intHello.sayHello mustBe "Hello"
    }


    "expand simple 1-arg Show SAM-like implementation" in {

      trait Show[T] {
        def show(x: T): String
      }

      val intShow: Show[Int] = sam[Show[Int]]((x: Int) => x.toString)

      intShow.show(5) mustBe "5"
    }

    "expand simple 2-arg Show SAM-like implementation" in {

      trait Eq[T] {
        def eq(x1: T, x2: T): Boolean
      }

      val intEq = sam[Eq[Int]]((x1: Int, x2: Int) => x1 == x2)

      intEq.eq(5, 5) mustBe true
    }
  }

}
