
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
      val (xxx: ValOrDefDef) :: Nil = annottees.map(_.tree).toList
      val (f: Function) = xxx.rhs.asInstanceOf[Function]

      val TC = xxx.tpt

      val TCName = TC.asInstanceOf[AppliedTypeTree].tpt.asInstanceOf[Ident].name

      // TODO: fix this
      val tcFullName = "samurai." + TCName

      val tcClz = c.mirror.staticClass(tcFullName)
      val abstractMembers = tcClz.typeSignature.members.filter(_.isAbstract)

      if(abstractMembers.size != 1) {
        c.abort(
          c.enclosingPosition,
          s"$tcClz has not single abstract method, but ${abstractMembers.size} ${abstractMembers.map(_.name).mkString("(",", ", ")")}"
        )
      } else {

        def samSymbol = abstractMembers.head.asMethod

        val samName = samSymbol.name.toTermName
        val samRetType = samSymbol.returnType
        val tree = q"${xxx.mods} val ${xxx.name}: ${xxx.tpe} = new $TC { def $samName(..${f.vparams}): $samRetType = ${f.body} }"

        c.Expr[Any](tree)
      }
    }

  }
}
