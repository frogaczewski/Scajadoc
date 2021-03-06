package org.scajadoc.page

import xml.Node
import tools.nsc.doc.model.{DocTemplateEntity, ConstantVal, MemberEntity, Package => ScalaPackage}
import org.scajadoc.util.{linkResolver, entityTreeTraverser}
import org.scajadoc.extractor.{FieldExtractor, TypeExtractor, entityQueryContainer}

/**
 * Generates constant-values.html file. 
 *
 * @author Filip Rogaczewski
 */
class ConstantValuesPage(val rootPackage : ScalaPackage) extends HtmlPage {

   import entityQueryContainer._

	def filename = "constant-values"

	def pageTitle = "Constant Field Values"

	def entity = this.rootPackage

	def headers = ""

	def body = {
		var body = Nil:List[Node]
		val constants = entityTreeTraverser
            .collect(rootPackage, isConstant).map(_.asInstanceOf[ConstantVal])
            .filter((c : ConstantVal) => (c.inheritedFrom.size == 0
               || (c.inheritedFrom.size > 0 && c.inheritedFrom.head == c.inTemplate)))
      body ++= constantHtmlUtil.constantsHeaderHtml
      val groupedConstants = constants.groupBy(c => c.inTemplate)
      groupedConstants.iterator.foreach((constDescr : (DocTemplateEntity, List[ConstantVal])) => {
         body ++= constantHtmlUtil.constantsTable(constDescr._1, constDescr._2, rootPackage)
      })
		body
	}

}

object constantHtmlUtil {

   private val fieldExtractor = new FieldExtractor

   def constantsHeaderHtml = {
      <hr /><center><h1>Constant Field Values</h1></center><hr />
   }

   def constantToHtml(constant : ConstantVal, from : DocTemplateEntity) = {
      val extract = fieldExtractor.extract(constant).get
      <tr bgcolor="white" class="TableRowColor">
         <a name={""}><!-- --></a>
         <td align="right"><font size="-1"><code>{extract.typ}</code></font></td>
         <td align="left"><code><a href={linkResolver.resolve(constant).get.link(from)}>{extract.name}</a></code></td>
         <td align="right"><code>{constant.value}</code></td>
      </tr>
   }

   def constantsTable(parent : DocTemplateEntity, constants : List[ConstantVal], from : DocTemplateEntity) = {
      <table border="1" cellpadding="3" cellspacing="0" summary="">
         <tr bgcolor="#EEEEFF" class="TableSubHeadingColor">
            <th align="left" colspan="3">{new TypeExtractor().extract(parent).get.inPackage.qualifiedName+"."}<a href={linkResolver.resolve(parent).get.link(from)}>{parent.rawName}</a></th>
         </tr>
         {constants.map(constantToHtml(_, from))}
      </table><br/>
   }

}