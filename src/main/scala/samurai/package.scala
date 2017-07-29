import scala.reflect.macros.whitebox

package object samurai {

  def sam[TC](f: () => Any): TC = macro SamuraiMacros.samImpl[TC]
  def sam[TC](f: Nothing => Any): TC = macro SamuraiMacros.samImpl[TC]
  def sam[TC](f: (Nothing, Nothing) => Any): TC = macro SamuraiMacros.samImpl[TC]

  private object SamuraiMacros {

    def samImpl[TC: c.WeakTypeTag]
      (c: whitebox.Context)
      (f: c.Tree): c.Tree = {

      import c.universe._

      println(s"enclosingPos: ${c.enclosingPosition}")

      val TC = weakTypeOf[TC]
//      val Args = weakTypeOf[Args]
//      val Ret = weakTypeOf[Ret]

      println("prefix is: " + c.prefix.tree)

      println("TC is: " + TC.toString())
//      println("Args is: " + Args.toString())
//      println("Ret is: " + Ret.toString())


      println("f is: " + f.toString())

      q"$f : $TC"
    }

  }



}

