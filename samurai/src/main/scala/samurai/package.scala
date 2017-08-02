
import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.language.experimental.macros

package object samurai {

  @compileTimeOnly("enable macro paradise to expand macro annotations")
  class sam extends StaticAnnotation {
    def macroTransform(annottees: Any*): Any = macro SamuraiMacros.instImpl
  }
}
