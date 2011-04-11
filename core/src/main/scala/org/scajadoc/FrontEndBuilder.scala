package org.scajadoc

import extractor.entityQueryContainer
import page._
import tools.nsc.doc.Universe
import tools.nsc.doc.model.DocTemplateEntity
import util.{resourceManager, entityTreeTraverser}

/**
 * Entry point for generating html front-end from universe created
 * by the model factory.
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
      resourceManager.copyResources
		indexPages.foreach(page => htmlPageWriter.write(page))
		entityTreeTraverser.collect(universe.rootPackage, isType).foreach(entity =>
         if (!entityQueryContainer.isMemberAnnotation(entity))
            htmlPageWriter.write(new TypePage(entity.asInstanceOf[DocTemplateEntity]))
		)
	}

}