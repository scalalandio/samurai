
import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.reflect.macros.whitebox

package object samurai {

  @compileTimeOnly("enable macro paradise to expand macro annotations")
  class sam extends StaticAnnotation {
    def macroTransform(annottees: Any*): Any = macro SamuraiMacros.instImpl
  }

  private object SamuraiMacros {

    // target: for 2.12+
    def identityImpl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
      import c.universe._
      val (xxx: ValOrDefDef) :: Nil = annottees.map(_.tree).toList
      c.Expr[Any](q"$xxx")
    }

    // target: for 2.11
    def instImpl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
      import c.universe._
      val (inst: ValOrDefDef) :: Nil = annottees.map(_.tree).toList
      val samFunction = inst.rhs.asInstanceOf[Function]

      val tc = inst.tpt
      val tcName = inst.tpt.asInstanceOf[AppliedTypeTree].tpt.asInstanceOf[Ident].name

      // TODO: fix this
      val tcFullName = "samurai." + tcName

      val tcClz = c.mirror.staticClass(tcFullName)
      val abstractMembers = tcClz.typeSignature.members.filter(_.isAbstract).toList

      abstractMembers match {
        case List(m) =>

          val samSymbol = m.asMethod

          val samName = samSymbol.name.toTermName
          val samRetType = samSymbol.returnType
          val tree = q"${inst.mods} val ${inst.name}: $tc = new $tc { def $samName(..${samFunction.vparams}): $samRetType = ${samFunction.body} }"

          c.Expr[Any](tree)

        case ms =>
          c.abort(
            c.enclosingPosition,
            s"$tcClz has not single abstract method, but ${ms.size} ${ms.map(_.name).mkString("(",", ", ")")}"
          )
      }
    }

  }
}
