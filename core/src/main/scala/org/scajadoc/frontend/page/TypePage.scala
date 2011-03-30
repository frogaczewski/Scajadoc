package org.scajadoc.frontend.page

import xml.Node
import tools.nsc.doc.model.{TemplateEntity, DocTemplateEntity, Package => ScalaPackage}
import org.scajadoc.frontend.{linkResolver, entityQueryContainer, entityPresentationUtil, classpathCache}

/**
 * @author Filip Rogaczewski
 */
class TypePage(val template : DocTemplateEntity) extends HtmlPage {

	import entityQueryContainer._

	def filename = template.name

	def entity = this.template

	def pageTitle = template.name

	def headers = ""

	def body = {
		var body = Nil:List[Node]
		body ++= header
		body
	}

   private def linkable(template : TemplateEntity) : Boolean = true

   private def makeTypeReference(template : TemplateEntity) = {
      /* if (linkable(template))
         //<a href={linkResolver.resolve(template).get.absoluteLink}>{template.name}</a>
      else */template.name
   }

	private def classesInheritanceTree() = {
      template.linearizationTemplates.filter(!_.isTrait).filter(!_.isInstanceOf[DocTemplateEntity]).foreach(e => println(e.qualifiedName))
		template.linearizationTemplates.filter(!_.isTrait).map(makeTypeReference(_))
	}

	private def allImplementedInterfaces() = {
		val interfaces = template.interfaces
      interfaces.filter(!_.isInstanceOf[DocTemplateEntity]).foreach(e => println(e.qualifiedName))
      interfaces.map(makeTypeReference(_)).mkString(",")
	}

	private def allSuperinterfaces() = {
		allImplementedInterfaces
	}

	private def allKnownImplementingClasses() = {
		template.subClasses.filter(!_.isTrait).map(makeTypeReference(_)).mkString(",")
	}

	private def directKnownSubclasses() = {
		template.subClasses.filter(!_.isTrait).map(makeTypeReference(_)).mkString(",")
	}

	private def enclosingClass() = {
		template.enclosingClass match {
			case Some(e) => e.name
			case None => ""
		}
	}

	private def header() =
		<!-- ======== START OF CLASS DATA ======== -->
		<h2>
		<font size="-1">{classpathCache(template).packageCanonicalPath}</font>
		<br/>
			{entityPresentationUtil.templateType(template)} {template.name}
		</h2>
		<pre>
			<!-- inhertitance tree-->
				<b>			{classesInheritanceTree}</b>
		</pre>
		<dl><dt><b>All Implemented Interfaces:</b>
			<dd>{allImplementedInterfaces}</dd></dt></dl>
		<dl><dt><b>Direct Known Subclasses:</b><dd>{allKnownImplementingClasses}</dd></dt></dl>
		<dl><dt><b>Enclosing class:</b>
			<dd>{enclosingClass}</dd>
		</dt></dl>
		<hr />
		<dl>
			<dt><pre><!-- signature --></pre></dt>
		</dl>
		<p>{entityPresentationUtil.short(template.comment)}</p>
		<hr/>

}