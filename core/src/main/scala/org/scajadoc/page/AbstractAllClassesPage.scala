package org.scajadoc.page

import tools.nsc.doc.model.{DocTemplateEntity, MemberEntity, Package => ScalaPackage}
import xml.{NodeBuffer, Node}
import org.scajadoc.extractor.{TypeExtract, TypeExtractor, entityQueryContainer}
import org.scajadoc.util.{linkResolver, entityPresentationUtil, entityTreeTraverser}

/**
 * Abstract base for generating allclasses-noframe.html and allclasses-frame.html files.
 *
 * @author Filip Rogaczewski
 */
abstract class AbstractAllClassesPage(val rootPackage : ScalaPackage) extends HtmlPage {

   import entityQueryContainer._

	def pageTitle = "All Classes"

	def entity = this.rootPackage

	def targetFrame : String

   val typeExtractor = new TypeExtractor

	def body = {
		<font size="+1" class="FrameHeadingFont"><b>All Classes</b></font><br/> ++
      <xml:group>{entityTreeTraverser.collect(rootPackage, isType)
            .sortBy(sort)
            .map(_.asInstanceOf[DocTemplateEntity])
            .map(e =>
               allTypesHtmlUtil.allClassesElementHtml(typeExtractor.extract(e).get, rootPackage, targetFrame))}
      </xml:group>
	}

}

object allTypesHtmlUtil {

   def allClassesElementHtml(extract : TypeExtract, from : DocTemplateEntity, target : String) = {
      val title = "%s in %s".format(extract.typ, extract.inPackage.qualifiedName)
      <a href={linkResolver.resolve(extract.entity).get.link(from)} title={title} target={target}>{extract.name}</a><br/>
   }

}