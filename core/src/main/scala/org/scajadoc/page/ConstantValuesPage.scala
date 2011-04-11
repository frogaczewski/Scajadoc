package org.scajadoc.page

import xml.Node
import tools.nsc.doc.model.{ConstantVal, MemberEntity, Package => ScalaPackage}
import org.scajadoc.util.entityTreeTraverser


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