package org.scajadoc.frontend.page

import tools.nsc.doc.model.DocTemplateEntity
import xml.Node
import org.scajadoc.frontend.{entityQueryContainer, entityPresentationUtil, classpathCache}

/**
 * @author Filip Rogaczewski
 */
class TypePage(val template : DocTemplateEntity) extends HtmlPage {

	import entityQueryContainer._

	def filename = template.name + ".html"

	def entity = this.template

	def pageTitle = template.name

	def headers = ""

	def body = {
		var body = Nil:List[Node]
		body ++= header
		body
	}


	private def allImplementedInterfaces() = {
		var interfaces = Nil:List[String]
		/* template.linearizationTemplates.filter(_.isTrait).foreach(e =>
			interfaces ::= e.name
		)*/
		template.interfaces.foreach(e =>
			interfaces ::= e.name
		)
		interfaces.mkString(",")
	}

	private def header() =
		<!-- ======== START OF CLASS DATA ======== -->
		<h2>
		<font size="-1">{classpathCache(template).packageCanonicalPath}</font>
		<br/>
			{entityPresentationUtil.templateType(template)}
		</h2>
		<pre>
			<!-- inhertitance tree-->
		</pre>
		<dl><dt><b>All Implemented Interfaces:</b>
			<dd>{allImplementedInterfaces}</dd></dt></dl>
		<dl><dt><b>Direct Known Subclasses:</b><dd></dd></dt></dl>
		<hr />
		<dl>
			<dt><pre><!-- signature --></pre></dt>
		</dl>
		<p>{entityPresentationUtil.short(template.comment)}</p>
		<hr/>

}