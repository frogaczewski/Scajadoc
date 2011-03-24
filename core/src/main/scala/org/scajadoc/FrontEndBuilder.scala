package org.scajadoc

import frontend.page._
import frontend.{entityQueryContainer, entityTreeTraverser, htmlPageWriter}
import tools.nsc.doc.Universe
import tools.nsc.doc.model.DocTemplateEntity

/**
 *
 * @author Filip Rogaczewski
 */
class FrontEndBuilder(val universe : Universe) {

	import entityQueryContainer._

	lazy val indexPages = {
		var pages = Nil:List[HtmlPage]
		pages ::= new SingleIndexPage(universe.rootPackage)
		pages ::= new AllClassesNoFramePage(universe.rootPackage)
		pages ::= new AllClassesFramePage(universe.rootPackage)
//		pages ::= new ConstantValuesPage(universe.rootPackage)
		pages ::= new DeprecatedListPage(universe.rootPackage)
		pages
	}


	def build() = {
		/* entityTreeTraverser.traverse(universe.rootPackage, doc => {
			println("canonical classpath " + classpathCache(doc).canonicalClasspath)
			println("package canonical path " + classpathCache(doc).packageCanonicalPath)
			println("package  doc canonical path " + classpathCache(doc).docPackageClasspath)
			println("doc classpath " + classpathCache(doc).docFileClasspath)
			htmlPageWriter.createFile(classpathCache(doc))
		}) */
//		documentableEntityMap.map(universe.rootPackage)
		indexPages.foreach(page => htmlPageWriter.write(page))
		entityTreeTraverser.collect(universe.rootPackage, isType).foreach(entity =>
			htmlPageWriter.write(new TypePage(entity.asInstanceOf[DocTemplateEntity]))
		)
//		entityTreeTraverser.collect(universe.rootPackage, isType)
	}

}