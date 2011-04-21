package org.scajadoc

import extractor.entityQueryContainer
import page._
import tools.nsc.doc.Universe
import tools.nsc.doc.model.DocTemplateEntity
import util.{resourceManager, entityTreeTraverser}
import collection.mutable.ListBuffer

/**
 * Entry point for generating html front-end from universe created
 * by the model factory.
 *
 * @author Filip Rogaczewski
 */
class FrontEndBuilder(val universe : Universe) {

	import entityQueryContainer._

	lazy val indexPages = {
      var pages = new ListBuffer[HtmlPage]
      pages += new SingleIndexPage(universe.rootPackage)
      pages += new AllClassesNoFramePage(universe.rootPackage)
      pages += new AllClassesFramePage(universe.rootPackage)
      pages += new DeprecatedListPage(universe.rootPackage)
      pages += new OverviewFramePage(universe.rootPackage)
      pages += new OverviewSummaryPage(universe.rootPackage)
		pages.toList
	}


	def build() = {
      resourceManager.copyResources
		indexPages.foreach(page => htmlPageWriter.write(page))
		entityTreeTraverser.collect(universe.rootPackage, isType).foreach(entity =>
         if (!entityQueryContainer.isMemberAnnotation(entity))
            htmlPageWriter.write(new TypePage(entity.asInstanceOf[DocTemplateEntity]))
		)
      entityTreeTraverser.collect(universe.rootPackage, isPackage)
         .map(_.asInstanceOf[DocTemplateEntity])
         .filter(isDocumentablePackage(_)).foreach(pack =>
            htmlPageWriter.write(new PackagePage(pack)))
	}

}