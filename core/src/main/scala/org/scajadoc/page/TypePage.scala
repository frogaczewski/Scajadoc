package org.scajadoc.page

import xml.{NodeSeq, Node}
import org.scajadoc.util._
import org.scajadoc.extractor._
import collection.mutable.ListBuffer
import tools.nsc.doc.model._
import collection.immutable.List._

/**
 * Generates page with information about class, enum or interface.
 *
 * @author Filip Rogaczewski
 */
class TypePage(val template : DocTemplateEntity) extends HtmlPage {

   import entityQueryContainer._

   private final val whitespace = " "

	def filename = template.name

	def entity = this.template

	def pageTitle = template.name

	def headers = ""

   /**
    * Extract of the pages template.
    */
   private val extract = new TypeExtractor().extract(template).get

   private val fieldExtractor = new FieldExtractor

   private val methodExtractor = new MethodExtractor

	def body = {
		var body = Nil:List[Node]
		body ++= header
      if (extract.isClass || extract.isEnum) {
         body ++= typeTree
         body ++= implementedInterfaces
         body ++= subClasses
         body ++= nestedClass
         body ++= signature
         body ++= comment
         body ++= tags
         body ++= fieldsSummary
         body ++= constructorSummary
         body ++= methodsSummary
         body ++= inheritedMethods
      } else if (extract.isInterface) {
         body ++= superInterfaces
         body ++= subInterfaces
         body ++= implementingClasses
         body ++= signature
         body ++= comment
         body ++= tags
         body ++= fieldsSummary
         body ++= methodsSummary
         body ++= fieldsDetail
         body ++= methodsDetail
      }
		body
	}

   /**
    * Write the header of this page. 
    */
	private def header() = {
		<!-- ======== START OF CLASS DATA ======== -->
		<h2>
		<font size="-1">{classpathCache(template).packageCanonicalPath}</font>
		<br/>
         {extract.typ.capitalize} {extract.name}
		</h2>
   }

   /**
    * Write the class tree documentation.
    */
   private def typeTree() = {
		<pre>{
         var tree = Nil:List[Node]
         var tabs = ""
         for (superclass <- extract.superClasses) {
            tree ++= typePageHtmlUtil.inheritNodeToHtml(tabs, superclass, template)
            tabs ++= whitespace
            tabs ++= whitespace
         }
         tree ++= typePageHtmlUtil.inheritNodeToHtml(tabs, template, template)
         tree
      }</pre>
   }

   /**
    * All implemented interfaces, in case this is a class.
    */
   private def implementedInterfaces() =
      typePageHtmlUtil.listOfTypesToHtml(extract.interfaces, "All Implemented Interfaces:", template)


   /**
    * All extended interfaces, in case this is an interface.
    */
   private def superInterfaces() =
      typePageHtmlUtil.listOfTypesToHtml(extract.interfaces, "All  Superinterfaces:", template)

   /**
    * All interfaces that extend this one.
    */
   private def subInterfaces() =
      typePageHtmlUtil.listOfTypesToHtml(extract.subinterfaces, "All Known Subinterfaces:", template)

   /**
    * All the classes that extend this one.
    */
   private def subClasses() =
      typePageHtmlUtil.listOfTypesToHtml(extract.subclasses, "Direct Known Subclasses:", template)


   /**
    * All the classes that implement this one.
    */
   private def implementingClasses() =
      typePageHtmlUtil.listOfTypesToHtml(extract.subclasses, "All Known Implementing Classes:", template)

   /**
    * If this is an inner class or interface, write the enclosing class
    * or interface.
    */
   private def nestedClass() = extract.enclosingClass match {
      case Some(enc) => <dl><dt><b>Enclosing class:</b>
         <dd>{typePageHtmlUtil.templateLinkToHtml(enc, template)}</dd></dt></dl>
      case None => NodeSeq.Empty
   }

   /**
    * Signature of this class.
    */
   private def signature() = {
      def headline() = {
         var sign = new ListBuffer[String]
         sign += extract.visibility
         if (extract.isAbstract)
            sign += "abstract"
         sign += extract.typ
         sign += extract.name
         <xml:node>{sign.mkString(whitespace)}</xml:node>
      }
      def interfaces() = {
         var interfacesXML = Nil:List[Node]
         val interfaces = extract.interfaces
         if (interfaces.isEmpty)
            NodeSeq.Empty
         else {
            interfacesXML ++= <br/>
            if (extract.isInterface) {
               interfacesXML ++= <xml:node>extends </xml:node>
            } else {
               interfacesXML ++= <xml:node>implements </xml:node>
            }
            interfacesXML ++= typePageHtmlUtil.templatesToHtmlLinks(interfaces, template)
         }
         interfacesXML
      }
      <hr/>
      <dl>
      <dt><pre>{
         var signature = Nil:List[Node]
         // sinature ++= annotations
         signature ++= headline
         if (extract.isClass || extract.isEnum) {
            signature ++= <br/>
            signature ++= <xml:node>extends </xml:node>
            signature ++= typePageHtmlUtil.templateLinkToHtml(extract.directSuperclass, template)
         }
         signature ++= interfaces
         signature
         }</pre></dt>
      </dl>
   }

   /**
    * Comment of this class.
    */
   private def comment() = {
      <xml:node>{entityPresentationUtil.full(template.comment)}</xml:node> ++ <hr/>
   }

   private def tags() = entityPresentationUtil.tags(template.comment)

   private def footer() = {

   }

   /**
    * Creates a list of field summaries.
    */
   private def fieldsSummary() : NodeSeq = {
      val fieldExtracts = template.members.filter(isField).map(e => {
         e match {
            case v : Val => fieldExtractor.extract(v)
            case _ => None
         }
      }).filter(_.isDefined).map(_.get)
      typePageHtmlUtil.summaryToHtml(fieldExtracts, "Field Summary", template)
   }

   /**
    * Creates a list of methods summaries.
    */
   private def methodsSummary() : NodeSeq = {
      val methodExtracts = template.members.filter(isMethod).map(e => {
         e match {
            case d : Def => methodExtractor.extract(d)
            case v : Val => methodExtractor.extract(v)
            case _ => None
         }
      }).filter(_.isDefined).map(_.get).filter(!_.isInherited)
      typePageHtmlUtil.summaryToHtml(methodExtracts, "Method Summary", template)
   }

   /**
    * Creates a list of constructor summaries.
    */
   private def constructorSummary() : NodeSeq = {
      val constructorExtracts = template.members.filter(isConstructor).map(e => {
         e match {
            case c : Constructor => methodExtractor.extract(c)
            case _ => None
         }
      }).filter(_.isDefined).map(_.get)
      typePageHtmlUtil.summaryToHtml(constructorExtracts, "Constructor Summary", template)
   }

   /**
    * Creates a list of inherited methods.
    */
   private def inheritedMethods() : NodeSeq = {
      def methodsForSupertype(extracts : List[InheritedMethodExtract], from : TemplateEntity) =
         extracts.filter(extract => extract.inDefinitionTemplates.map(_.qualifiedName).contains(from.qualifiedName))
      val methodExtracts = template.members.filter(isMethod).map(e => {
         e match {
            case d : Def => methodExtractor.extract(d)
            case v : Val => methodExtractor.extract(v)
            case _ => None
         }
      }).filter(_.isDefined).map(_.get).filter(_.isInherited).map(_.asInstanceOf[InheritedMethodExtract])
      val supertypes = extract.superClasses ++ extract.interfaces
      var supertypesMethodsHtml = Nil:List[Node]
      supertypes.foreach(superType => {
         supertypesMethodsHtml ++= typePageHtmlUtil.listOfInheritedMethodsToHtml(
            methodsForSupertype(methodExtracts, superType), superType, template)
      })
      supertypesMethodsHtml
   }

   private def fieldsDetail() : NodeSeq = {
      NodeSeq.Empty
   }

   private def methodsDetail() : NodeSeq = {
      NodeSeq.Empty
   }

}

/**
 * Utility class with methods for
 *
 * @author Filip Rogaczewski
 */
object typePageHtmlUtil {

   /**
    * Makes a html summary of the field.
    */
   def extractSummary[M <: MemberExtract](member : M, from : DocTemplateEntity) : NodeSeq = {
      <tr bgcolor="white" class="TableRowColor">
         <td align="right" valign="top" width="1%">
            <font size="-1">
               <code>{member.allocation.toString} {member.typ}</code>
            </font>
         </td>
         <td>
            <code><b>{templateLinkToHtml(member.entity, from, member)}</b></code>
            <br/>{entityPresentationUtil.short(member.entity.comment)}
         </td>
      </tr>
   }


   /**
    * Creates HTML node of an inheritance tree.
    */
   def inheritNodeToHtml(tabs : String, t : TemplateEntity, from : DocTemplateEntity) : NodeSeq = {
      var tree = Nil:List[Node]
      tree ++= <xml:node>{tabs}</xml:node>
      if (!tabs.isEmpty)
         tree ++= <img src={resourceManager.inheritGif()} alt="extended by " />
      tree ++= templateLinkToHtml(t, from)
      tree ++= <br/>
      tree
   }

   /**
    * Builds link to the template.
    */
   def templateLinkToHtml(t : Entity, from : DocTemplateEntity) : NodeSeq = {
      val link = linkResolver.resolve(t)
      link match {
         case Some(l) => <a href={l.link(from)}>{t.rawName}</a>
         case None => <xml:node>{t.rawName}</xml:node>
      }
   }

   /**
    * Builds link to the template.
    */
   def templateLinkToHtml(t : Entity, from : DocTemplateEntity, extract : Extract) : NodeSeq = {
      val link = linkResolver.resolve(t)
      link match {
         case Some(l) => <a href={l.link(from)}>{extract.name}</a>
         case None => <xml:node>{extract.name}</xml:node>
      }
   }

   def templatesToHtmlLinks(entities : List[Entity], from : DocTemplateEntity) = {
      var result = Nil:List[Node]
      result ++= <xml:group>{entities.init.map(templateLinkToHtml(_, from) ++ <xml:node>, </xml:node>)}</xml:group>
      result ++= templateLinkToHtml(entities.last, from)
      result
   }

   def extractsToHtmlLinks(extracts : List[MemberExtract], from : DocTemplateEntity) = {
      var result = Nil:List[Node]
      result ++= <xml:group>{extracts.init.map(ex => templateLinkToHtml(ex.entity, from, ex) ++ <xml:node>, </xml:node>)}</xml:group>
      result ++= templateLinkToHtml(extracts.last.entity, from, extracts.last)
      result
   }


   /**
    * Builds a summary.
    */
   def summaryToHtml[M <: MemberExtract](extracts : List[M], summary : String, from : DocTemplateEntity) : NodeSeq = {
      if (extracts.isEmpty)
         NodeSeq.Empty
      else {
         var summaryHtml = Nil:List[Node]
         summaryHtml ++= <!-- =========== {summary} =========== -->
         summaryHtml ++= <a name={summary.replace(" ", "_")}><!-- --></a>
         summaryHtml ++= <table border="1" width="100%" cellpadding="3" cellspacing="0" summary="">
            <tr bgcolor="#CCCCFF" class="TableHeadingColor">
               <th align="left" colspan="2"><font size="+2"><b>{summary}</b></font></th>
            </tr> {extracts.map(extractSummary(_, from))} </table>
         summaryHtml ++= <br/>
         summaryHtml
      }
   }

   def listOfTypesToHtml(types : List[TemplateEntity], title : String, from : DocTemplateEntity) = {
      if (types.isEmpty)
         NodeSeq.Empty
      else {
		   <dl><dt><b>{title}</b>
            <dd>{templatesToHtmlLinks(types, from)}</dd></dt></dl>
      }
   }

   def listOfInheritedMethodsToHtml(inhMethods : List[InheritedMethodExtract], from : TemplateEntity, tmp : DocTemplateEntity) = {
      if (inhMethods.isEmpty)
          NodeSeq.Empty
      else {
         <table border="1" width="100%" cellpadding="3" cellspacing="0" summary="">
            <tr bgcolor="#EEEEFF" class="TableSubHeadingColor">
               <th align="left"><b>Methods inherited from class {from.qualifiedName}</b></th>
            </tr>
            <tr bgcolor="white" class="TableRowColor">
               <td>{extractsToHtmlLinks(inhMethods, tmp)}
               </td>
            </tr>
         </table>
      }
   }

}