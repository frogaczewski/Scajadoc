package org.scajadoc

import frontend.htmlPageWriter
import frontend.page._
import tools.nsc.doc.Universe

/**
 *
 * @author Filip Rogaczewski
 */
class FrontEndBuilder(val universe : Universe) {

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
	}

}