package org.scajadoc.util

import tools.nsc.doc.model.{MemberEntity, DocTemplateEntity, Entity}
import java.io.File
import tools.nsc.io.Directory

/**
 * Utility class for tests which requires non-mocked template.
 *
 * @author Filip Rogaczewski
 */
class TemplateGenerator {

   /**
    * Document factory from the Scala compiler.
    */
   private val docFactory = {
      val settings = new tools.nsc.doc.Settings((s : String) => System.err.println(s))
      settings.classpath.value = {
         val scalaLib = System.getenv("SCALA_HOME") + "\\lib"
         List(
				new File(scalaLib, "scala-library.jar").getAbsolutePath,
				new File(scalaLib, "scala-compiler.jar").getAbsolutePath
			).mkString(File.pathSeparator)
      }
      val reporter = new tools.nsc.reporters.ConsoleReporter(settings)
      new tools.nsc.doc.DocFactory(reporter, settings)
   }

   /**
    * Generates and returns the entity.
    */
   def generate(path : String, name : String) : List[MemberEntity] = {
      val sourcepath = new Directory(new File(path)).deepFiles.filter(_.extension == "scala").map(_.path).toList
      val universe = docFactory.universe(sourcepath).get
      entityTreeTraverser.collect(universe.rootPackage,
         (entity : MemberEntity) => entity.name == name)
   }

}