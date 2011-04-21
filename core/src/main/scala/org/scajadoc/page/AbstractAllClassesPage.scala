package org.scajadoc.page

import tools.nsc.doc.model.{DocTemplateEntity, MemberEntity, Package => ScalaPackage}
import xml.{NodeBuffer, Node}
import org.scajadoc.util.{entityPresentationUtil, classpathCache, entityTreeTraverser}
import org.scajadoc.extractor.entityQueryContainer

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

	def body = {
		var body = Nil:List[Node]
		body ++= <font size="+1" class="FrameHeadingFont"><b>All Classes</b></font>
		body ++= <br/>
		entityTreeTraverser.collect(rootPackage, isType).sortBy(sort).foreach(e =>
			body ++= new LinkedType {
				override def name = e.rawName
				override def link = classpathCache(e).docBaseFileClasspath + ".html"
				override def title = "%s in %s".format(entityPresentationUtil.templateType(e.asInstanceOf[DocTemplateEntity]), entityPresentationUtil.inPackage(e.asInstanceOf[DocTemplateEntity]))
				override def target = targetFrame
			}
		)
		body
	}

	implicit def linkedTypeToHtml(typ : LinkedType) : NodeBuffer = typ.toHTML

}

/**
 * Representation of linked type.
 *
 * @author Filip Rogaczewski
 */
trait LinkedType {

	def name : String

	def link : String

	def title : String

	def target : String

	def toHTML = <a href={link} title={title} target={target}>{name}</a><br/>
}