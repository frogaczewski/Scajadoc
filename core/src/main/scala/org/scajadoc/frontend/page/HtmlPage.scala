package org.scajadoc.frontend.page

import java.nio.channels.Channels
import java.io.{File, FileOutputStream}
import xml.NodeSeq
import org.scajadoc.frontend.Classpath
import tools.nsc.doc.model.MemberEntity
import org.scajadoc.settings

/**
 * Trait for html pages. 
 *
 * @author Filip Rogaczewski
 */
trait HtmlPage {

	/**
	 * Sorts member entities collected by the collectCondition.
	 * Constructors are sorted by theirs template's raw name, other entities are sorted by theirs raw name.
	 */
	val sort : (MemberEntity => String) =
		(t : MemberEntity) => if (t.isConstructor) t.inTemplate.rawName.toLowerCase else t.rawName.toLowerCase	

	def encoding: String = "UTF-8"

	def entity : MemberEntity

	def pageTitle : String

	final def title = pageTitle + " (%s)".format(settings.javadocTitle)

	def headers : String

	def body : NodeSeq

	def filename : String

   def file = filename + settings.outputFormat

	def html =
		<html>
			<head>
				<title>{ title }</title>
				<meta http-equiv="content-type" content={ "text/html; charset=" + encoding }/>
				{ headers }
			</head>
			{ body }
		</html>

}