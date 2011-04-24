package org.scajadoc.page

import xml.{Node, Elem}
import org.scajadoc.util.{entityPresentationUtil, entityTreeTraverser}
import org.scajadoc.extractor._
import tools.nsc.doc.model.{NonTemplateMemberEntity, DocTemplateEntity, Val, Def, Constructor, MemberEntity, Package => ScalaPackage}

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
         javaExtractor.extract(e) match {
            case Some(ext) => {
               var description = ""
               ext match {
                  case t : TypeExtract => description = singleIndexPageHtmlUtil.typeDescription(t)
                  case f : FieldExtract => description = singleIndexPageHtmlUtil.fieldDescription(f)
                  case c : ConstructorExtract => description = singleIndexPageHtmlUtil.constructorDescription(c)
                  case m : MethodExtract => description = singleIndexPageHtmlUtil.methodDescription(m)
                  case _ => {}
               }
               body ++= singleIndexPageHtmlUtil.extractToIndexableHtml(ext, description)
            }
            case None => {}
         }
      )
			/* if (e.isConstructor) {
            body ++= singleIndexPageHtmlUtil.extractToIndexableHtml(
               javaExtractor.extract(e, singleIndexPageHtmlUtil.constructorDescription)
			} else if (e.isDef && entityPresentationUtil.isDocumentable(e.asInstanceOf[Def])) {
				body ++= singleIndexPageHtmlUtil.extractToIndexableHtml(
               javaExtractor.extract(e, )
            )
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
		)    */
		body
	}

	implicit def indexableToXML(i : Indexable[_]) : Elem = i.toHTML
	
}

object singleIndexPageHtmlUtil {

   def constructorDescription(extract : MethodExtract) = {
      "- Constructor for class " + extract.inTemplate.qualifiedName
   }

   def methodDescription(extract : MethodExtract) = {
      " - %s in %s %s".format((extract.allocation.toString + " method").trim.capitalize,
						extract.inTemplate.qualifiedName, extract.name)
   }

   def fieldDescription(extract : FieldExtract) = {
      " - %s in %s %s".format((extract.allocation.toString + " variable").trim.capitalize,
						extract.inTemplate.qualifiedName, extract.name)
   }

   def typeDescription(extract : TypeExtract) = {
      " - %s in %s".format(extract.typ, extract.inPackage.qualifiedName)
   }

   def extractToIndexableHtml(extract : Extract, description : String) = {
      <dl><dt><b>{extract.name}</b></dt><dd>{description} {entityPresentationUtil.short(extract.entity.comment)} </dd></dl>
   }

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