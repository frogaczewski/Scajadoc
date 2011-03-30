package org.scajadoc.frontend
package page

import xml.{Node, Elem}
import tools.nsc.doc.model.{DocTemplateEntity, Val, Def, Constructor, MemberEntity, Package => ScalaPackage}

/**
 * Generate index-all.html file.  
 *
 * @author Filip Rogaczewski
 */
class SingleIndexPage(val rootPackage : ScalaPackage) extends HtmlPage {

	/**
	 * <p>Function for collecting documentable entities</p>
	 * <p>Those entities are:
	 * <ul>
	 * <li>Defs - definition (seen as methods)</li>
	 * <li>Constructors</li>
	 * <li>Values and variables</li>
	 * <li>Others templates</li>
	 * </p>
	 */
	val collectCondition : (MemberEntity => Boolean) =
		(t : MemberEntity) => t.isDef || t.isConstructor || t.isVal || t.isTemplate || t.isVar

	def filename = "index-all"

	def pageTitle = "Index"

	def entity = this.rootPackage

	def headers = ""

	def body = {
		var body = Nil:List[Node]
		entityTreeTraverser.collect(rootPackage, collectCondition).sortBy(sort).foreach(e =>
			if (e.isConstructor) {
				body ++= new Indexable[Constructor] {
					override def entity = e.asInstanceOf[Constructor]
					override def name = entity.inTemplate.rawName + entityPresentationUtil.methodParams(entity.valueParams)
					override def description = " - Constructor for class " + entity.inTemplate.name
				}
			} else if (e.isDef && entityPresentationUtil.isDocumentable(e.asInstanceOf[Def])) {
				body ++= new Indexable[Def] {
					override def entity = e.asInstanceOf[Def]
					override def name = entity.rawName + entityPresentationUtil.methodParams(entity.valueParams)
					override def description = " - %s in %s %s".format(entityPresentationUtil.methodType(entity),
						entityPresentationUtil.inType(entity), entity.inTemplate.rawName)
				}
			} else if (e.isVal) {
				body ++= new Indexable[Val] {
					override def entity = e.asInstanceOf[Val]
					override def name = entity.rawName
					override def description = " - %s in %s %s".format(entityPresentationUtil.variableType(entity),
						entityPresentationUtil.inType(e), entity.inTemplate.rawName)
				}
			} else if (e.isTemplate && !e.asInstanceOf[DocTemplateEntity].isPackage) {
				body ++= new Indexable[DocTemplateEntity] {
					override def entity = e.asInstanceOf[DocTemplateEntity]
					override def name = entity.rawName
					override def description = " - %s in %s".format(entityPresentationUtil.templateType(entity),
						entityPresentationUtil.inPackage(entity))
				}
			}
		)
		body
	}

	implicit def indexableToXML(i : Indexable[_]) : Elem = i.toHTML
	
}

/**
 * Interface for indexable entities data holder.
 *
 * @author Filip Rogaczewski
 */
trait Indexable[E <: MemberEntity] {

	def name : String

	def entity : E

	def description : String

	def firstSentence : String = entityPresentationUtil.short(entity.comment)

	def toHTML : xml.Elem = <dl><dt><b>{ name }</b></dt><dd>{description} {firstSentence} </dd></dl>

}