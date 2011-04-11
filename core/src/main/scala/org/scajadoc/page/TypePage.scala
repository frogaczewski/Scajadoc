package org.scajadoc.page

import org.scajadoc.extractor.javaExtractor
import tools.nsc.doc.model.{TemplateEntity, DocTemplateEntity, Package => ScalaPackage}
import xml.{NodeSeq, Node}
import org.scajadoc.util.{resourceManager, linkResolver, entityPresentationUtil, classpathCache}

/**
 * @author Filip Rogaczewski
 */
class TypePage(val template : DocTemplateEntity) extends HtmlPage {

	def filename = template.name

	def entity = this.template

	def pageTitle = template.name

	def headers = ""

   val extract = javaExtractor.extractTypeInfo(template).get

	def body = {
		var body = Nil:List[Node]
		body ++= header
      if (extract.isClass || extract.isEnum) {
         body ++= typeTree
         body ++= implementedInterfaces
         body ++= subClasses
         body ++= nestedClass
         body ++= signature
      } else if (extract.isInterface) {
         body ++= superInterfaces
         body ++= subInterfaces
         body ++= implementingClasses
         body ++= signature
      }
		body
	}

   /**
    * Write the header of this page. 
    */
	private def header() =
		<!-- ======== START OF CLASS DATA ======== -->
		<h2>
		<font size="-1">{classpathCache(template).packageCanonicalPath}</font>
		<br/>
         {extract.typ.capitalize} {extract.name}
		</h2>

   /**
    * Write the class tree documentation.
    */
   private def typeTree() =
		<pre>{
         var tree = Nil:List[Node]
         var tabs = ""
         for (superclass <- extract.superClasses) {
            tree ++= inheritNodeToHtml(tabs, superclass)
            tabs ++= "  "
         }
         tree ++= inheritNodeToHtml(tabs, template)
         tree
      }</pre>

   /**
    * All implemented interfaces, in case this is a class.
    */
   def implementedInterfaces() =
      <dl><dt><b>All Implemented Interfaces:</b><dd>{extract.interfaces.map(templateLinkToHtml(_))}</dd></dt></dl>


   /**
    * All extended interfaces, in case this is an interface.
    */
   private def superInterfaces() = {
      val interfaces = extract.interfaces
      if (interfaces.isEmpty)
         NodeSeq.Empty
      else {
         <dl><dt><b>All  Superinterfaces:</b>
            <dd>{interfaces.map(templateLinkToHtml(_))}</dd></dt></dl>
      }
   }

   /**
    * All interfaces that extend this one.
    */
   private def subInterfaces() = {
      val subinterfaces = extract.subinterfaces
      if (subinterfaces.isEmpty)
         NodeSeq.Empty
      else {
 		   <dl><dt><b>All Known Subinterfaces:</b>
			   <dd>{subinterfaces.map(templateLinkToHtml(_))}</dd></dt></dl>
      }
   }

   /**
    * All the classes that extend this one.
    */
   private def subClasses() = {
      val subclasses = extract.subclasses
      if (subclasses.isEmpty)
         NodeSeq.Empty
      else {
         <dl><dt><b>Direct Known Subclasses:</b>
            <dd>{subclasses.map(templateLinkToHtml(_))}</dd></dt></dl>
      }
   }


   /**
    * All the classes that implement this one.
    */
   private def implementingClasses() =
		<dl><dt><b>All Known Implementing Classes:</b>
         <dd>{extract.subclasses.map(templateLinkToHtml(_))}</dd></dt></dl>

   /**
    * If this is an inner class or interface, write the enclosing class
    * or interface.
    */
   private def nestedClass() = extract.enclosingClass match {
      case Some(enc) => <dl><dt><b>Enclosing class:</b>
         <dd>{templateLinkToHtml(enc)}</dd></dt></dl>
      case None => NodeSeq.Empty
   }

   /**
    * Signature of this class.
    */
   private def signature() = {
		<hr />
		<dl>
			<dt><pre><!-- signature --></pre></dt>
		</dl>
   }

   /**
    * Comment of this class.
    */
   private def comment() = {
      <p>{entityPresentationUtil.short(template.comment)}</p>
		<hr/>
   }

   private def footer() = {

   }

   private def templateLinkToHtml(t : TemplateEntity) : NodeSeq = {
      val link = linkResolver.resolve(t)
      link match {
         case Some(l) => <a href={l.absoluteLink}>{t.name}</a>
         case None => <xml:group>{t.name}</xml:group>
      }
   }

   private def inheritNodeToHtml(tabs : String, t : TemplateEntity) : NodeSeq = {
      var tree = Nil:List[Node]
      tree ++= <xml:node>{tabs}</xml:node>
      if (!tabs.isEmpty)
         tree ++= <img src={resourceManager.inheritGif} alt="extended by " />
      tree ++= templateLinkToHtml(t)
      tree ++= <br/>
      tree
   }

}