package samurai

import scala.reflect.macros.whitebox

object SamuraiMacros {

  def instImpl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    val (inst: ValOrDefDef) :: Nil = annottees.map(_.tree).toList
    val samFunction = inst.rhs.asInstanceOf[Function]

    val (tcName, wildcards) = inst.tpt match {
      case att: AppliedTypeTree =>
        (att.tpt.asInstanceOf[Ident].name, att.args.map(_ => "_").mkString("[", ",", "]"))
      case idt: Ident =>
        (idt.name, "")
    }

    val ttt = c.parse(s"val x: $tcName$wildcards = null").asInstanceOf[ValDef]
    val tcFullName = c.typecheck(q"??? : ${ttt.asInstanceOf[ValDef].tpt}").tpe.typeSymbol.fullName
    val tcClz = c.mirror.staticClass(tcFullName)
    val abstractMembers = tcClz.typeSignature.members.filter(_.isAbstract)

    abstractMembers.toList match {
      case List(m) =>

        val samSymbol = m.asMethod
        val samName = samSymbol.name.toTermName
        val samRetType = samSymbol.returnType

        val samDefTree = q"def $samName(..${samFunction.vparams}): $samRetType = ${samFunction.body}"
        val instRhs = q"new ${inst.tpt} { $samDefTree }"

        val tree =
          inst match {
            case dd: DefDef =>
              q"${dd.mods} def ${dd.name}[..${dd.tparams}](...${dd.vparamss}): ${dd.tpt} = $instRhs"

            case vd: ValDef =>
              q"${vd.mods} val ${vd.name}: ${vd.tpt} = $instRhs"
          }

        c.Expr[Any](tree)

      case ms =>
        c.abort(
          c.enclosingPosition,
          s"$tcClz has not single abstract method, but ${ms.size} ${ms.map(_.name).mkString("(",", ", ")")}"
        )
    }
  }
}
