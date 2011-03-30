package org.scajadoc.frontend.page

import xml.Node
import org.scajadoc.frontend.entityTreeTraverser
import tools.nsc.doc.model.{ConstantVal, Val, MemberEntity, Package => ScalaPackage}

/**
 * Generates constant-values.html file. 
 *
 * @author Filip Rogaczewski
 */
class ConstantValuesPage(val rootPackage : ScalaPackage) extends HtmlPage {

	/* final val collectConstants : (MemberEntity => Boolean) =
			(entity : MemberEntity) => entity.isInstanceOf[Val] && entity.asInstanceOf[Val].isFinal && entity.inTemplate.isObject */

	final val collectConstants : (MemberEntity => Boolean) =
		(entity : MemberEntity) => entity.isInstanceOf[ConstantVal]

	def filename = "constant-values"

	def pageTitle = "Constant Field Values"

	def entity = this.rootPackage

	def headers = ""

	def body = {
		var body = Nil:List[Node]
		entityTreeTraverser.collect(rootPackage, collectConstants).foreach(entity =>
			println(entity.inTemplate.rawName + " " + entity.rawName)
		)
		body
	}

}