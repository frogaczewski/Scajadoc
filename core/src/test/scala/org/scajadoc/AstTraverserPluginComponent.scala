package org.scajadoc

import tools.nsc.Global
import tools.nsc.Phase
import tools.nsc.plugins.{Plugin, PluginComponent}
import com.sun.org.apache.xalan.internal.xsltc.compiler.sym

/**
 * Plugin which traverses Abstract Syntax Tree generated
 * by Scala compiler.
 *
 * @author Filip Rogaczewski
 */
class AstTraverserPlugin(val global : Global) extends Plugin {

   import global._

   val name = "AstTraverser"
   val description = "Prints Abstract Syntax Tree"
   val components = List[PluginComponent](AstTraverserPluginComponent)

   /**
    * Plugin component for traversing AST.
    */
   private object AstTraverserPluginComponent extends PluginComponent {

      /**
       * Compiler instance.
       */
      val global : AstTraverserPlugin.this.global.type = AstTraverserPlugin.this.global

      /**
       * Runs after all the references are checked.
       */
      override val runsAfter = List("refchecks")

      /**
       * Name of this compiler phase.
       */
      override val phaseName = "ast traverser-phase"

      def newPhase(previous : Phase) = {
         new Phase(previous) {
            def name = phaseName
            def run() = {
               for (u <- global.currentRun.units) {
                  new AstTraverser().traverse(u.body)
               }
            }
         }
      }
   }

   /**
    * Traverser which prints the name of processed symbol along with
    * the symbol's members.
    */
   class AstTraverser extends Traverser {
      override def traverse(tree : Tree) : Unit = {
         tree match {
            case DefDef(_, _, _, _, _, _) =>
               printSymbol("Method definition ", tree)
            case ClassDef(_, _, _, _) =>
               printSymbol("Class definition ", tree)
            case ValDef(_, _, _, _) =>
               printSymbol("Value definition ", tree)
            case _ => {printSymbol("", tree)}
         }
         tree.children.foreach(traverse(_))
      }

      /**
       * Prints the symbol.
       */
      private def printSymbol(title : String, tree : Tree) = {
         if (tree.symbol != null)
            println(title + " " + tree.symbol + " " + tree.getClass.getSimpleName)
      }
   }
}