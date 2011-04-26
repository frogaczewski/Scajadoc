package org.scajadoc.page

import xml.Node
import org.scajadoc.extractor._
import tools.nsc.doc.model.{MemberEntity, Package => ScalaPackage}
import org.scajadoc.util.{NavigationBarHtmlUtil, entityPresentationUtil, entityTreeTraverser}

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
      implicit def stringToOption(text : String) = Some(text)
		var body = Nil:List[Node]
      body ++= singleIndexPageHtmlUtil.navigationBarHtml(entity, simpleNavigation = true, index = true)
		entityTreeTraverser.collect(rootPackage, collectCondition).sortBy(sort)
         .map(javaExtractor.extract(_)).collect {case x if (x.isDefined) => x.get}
         .foreach(ext => {
            var description : Option[String] = None
            ext match {
               case t : TypeExtract => description = singleIndexPageHtmlUtil.typeDescription(t)
               case f : FieldExtract => description = singleIndexPageHtmlUtil.fieldDescription(f)
               case c : ConstructorExtract => description = singleIndexPageHtmlUtil.constructorDescription(c)
               case m : MethodExtract => {
                  if (!m.isInherited)
                     description = singleIndexPageHtmlUtil.methodDescription(m)
               }
               case _ => {}
            }
            if (description.isDefined)
               body ++= singleIndexPageHtmlUtil.extractToIndexableHtml(ext, description.get)
      })
		body
	}

}

/**
 * Html utils for single index page.
 *
 * @author Filip Rogaczewski
 */
object singleIndexPageHtmlUtil extends NavigationBarHtmlUtil {

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