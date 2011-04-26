package org.scajadoc

import extractor.entityQueryContainer
import page._
import tools.nsc.doc.Universe
import tools.nsc.doc.model.DocTemplateEntity
import collection.mutable.ListBuffer
import util.{typeSlider, resourceManager, entityTreeTraverser}
import collection.immutable.List._

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
      pages += new ConstantValuesPage(universe.rootPackage)
		pages.toList
	}


	def build() = {
      val types = entityTreeTraverser.collect(universe.rootPackage, isType).map(_.asInstanceOf[DocTemplateEntity])
      val packages = entityTreeTraverser.collect(universe.rootPackage, isPackage).map(_.asInstanceOf[DocTemplateEntity]).filter(isDocumentablePackage(_))
      typeSlider.init(types, packages)
      resourceManager.copyResources
      val writer = new HtmlPageWriter(universe.rootPackage)
		indexPages.foreach(page => writer.write(page))
		types.foreach(entity =>
         writer.write(new TypePage(entity.asInstanceOf[DocTemplateEntity]))
		)
      (universe.rootPackage :: packages).foreach(e => {
            writer.write(new PackagePage(e))
            writer.write(new PackageSummary(e))
            writer.write(new PackageTree(e))
      })
	}

}