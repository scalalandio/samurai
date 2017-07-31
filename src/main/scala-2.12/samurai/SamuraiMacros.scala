package samurai

import scala.reflect.macros.whitebox

object SamuraiMacros {

  def instImpl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    val (inst: ValOrDefDef) :: Nil = annottees.map(_.tree).toList
    c.Expr[Any](q"$inst")
  }
}
