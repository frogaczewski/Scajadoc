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

	def body = {
		var body = Nil:List[Node]
      body ++= typePageHtmlUtil.navigationBarHtml(template)
		body ++= header
      if (extract.isClass || extract.isEnum) {
         body ++= typeTree
         body ++= implementedInterfaces
         body ++= subClasses
         body ++= nestedClass
         body ++= signature
         body ++= typePageHtmlUtil.comment(template)
         body ++= tags
         body ++= fieldsSummary(extract.fields)
         body ++= inheritedFields(extract.inheritedFields)
         body ++= constructorSummary(extract.constructors)
         body ++= methodsSummary(extract.methods)
         body ++= inheritedMethods(extract.inheritedMethods)
         body ++= fieldsDetail(extract.fields)
         body ++= constructorsDetail(extract.constructors)
         body ++= methodsDetail(extract.methods)
      } else if (extract.isInterface) {
         body ++= superInterfaces
         body ++= subInterfaces
         body ++= implementingClasses
         body ++= signature
         body ++= typePageHtmlUtil.comment(template)
         body ++= tags
         body ++= fieldsSummary(extract.fields)
         body ++= methodsSummary(extract.methods)
         body ++= fieldsDetail(extract.fields)
         body ++= methodsDetail(extract.methods)
      }
		body
	}

   /**
    * Write the header of this page. 
    */
	private def header() = {
		<!-- ======== START OF CLASS DATA ======== -->
		<h2>
		<font size="-1">{extract.inPackage.qualifiedName}</font>
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
         if ((extract.isClass || extract.isEnum) && extract.directSuperclass != null) {
            signature ++= <br/>
            signature ++= <xml:node>extends </xml:node>
            signature ++= typePageHtmlUtil.templateLinkToHtml(extract.directSuperclass, template)
         }
         signature ++= interfaces
         signature
         }</pre></dt>
      </dl>
   }

   private def tags() = entityPresentationUtil.tags(template.comment)

   /**
    * Creates a list of field summaries.
    */
   private def fieldsSummary(fields : List[FieldExtract]) : NodeSeq = {
      typePageHtmlUtil.summaryToHtml(fields, "Field Summary", template)
   }

   /**
    * Creates a list of methods summaries.
    */
   private def methodsSummary(methods : List[MethodExtract]) : NodeSeq =
      typePageHtmlUtil.summaryToHtml(methods, "Method Summary", template)

   /**
    * Creates a list of constructor summaries.
    */
   private def constructorSummary(constructors : List[MethodExtract]) : NodeSeq =
      typePageHtmlUtil.summaryToHtml(constructors, "Constructor Summary", template)

   private def inheritedMethods(extracts : List[InheritedMember]) : NodeSeq =
      inheritedExtracts("Methods", extracts)

   private def inheritedFields(extracts : List[InheritedMember]) : NodeSeq =
      inheritedExtracts("Fields", extracts)

   /**
    * Creates a list of inherited methods.
    */
   private def inheritedExtracts(inheritedType : String, extracts : List[InheritedMember]) : NodeSeq = {
      def inheritedForSupertype(extracts : List[InheritedMember], from : TemplateEntity) = {
         extracts.filter(extract => extract.inDefinitionTemplates.map(_.qualifiedName).contains(from.qualifiedName))
      }
      val supertypes = extract.superClasses ++ extract.interfaces
      var inheritedHtml = Nil:List[Node]
      supertypes.foreach(superType => {
         inheritedHtml ++= typePageHtmlUtil.listOfInheritedToHtml(inheritedType,
            inheritedForSupertype(extracts, superType), superType, template)
      })
      inheritedHtml ++= <br/>
      inheritedHtml
   }

   private def fieldsDetail(fields : List[FieldExtract]) : NodeSeq = {
      typePageHtmlUtil.detailsToHtml(fields, "Field Detail", template)
   }

   private def methodsDetail(methods : List[MethodExtract]) : NodeSeq = {
      typePageHtmlUtil.detailsToHtml(methods, "Method Detail", template)
   }

   private def constructorsDetail(constructors : List[MethodExtract]) : NodeSeq = {
      typePageHtmlUtil.detailsToHtml(constructors, "Constructor Detail", template)
   }

}

/**
 * Utility class with methods for
 *
 * @author Filip Rogaczewski
 */
object typePageHtmlUtil extends NavigationBarHtmlUtil {

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

   /**
    * Builds html list of types.
    */
   def listOfTypesToHtml(types : List[TemplateEntity], title : String, from : DocTemplateEntity) = {
      if (types.isEmpty)
         NodeSeq.Empty
      else {
		   <dl><dt><b>{title}</b>
            <dd>{templatesToHtmlLinks(types, from)}</dd></dt></dl>
      }
   }

   /**
    * List of inherited methods to html.
    */
   def listOfInheritedToHtml(inheritedType : String, inhMethods : List[InheritedMember], from : TemplateEntity, tmp : DocTemplateEntity) = {
      if (inhMethods.isEmpty)
          NodeSeq.Empty
      else {
         <p><table border="1" width="100%" cellpadding="3" cellspacing="0" summary="">
            <tr bgcolor="#EEEEFF" class="TableSubHeadingColor">
               <th align="left"><b>{inheritedType} inherited from class {from.qualifiedName}</b></th>
            </tr>
            <tr bgcolor="white" class="TableRowColor">
               <td>{extractsToHtmlLinks(inhMethods, tmp)}
               </td>
            </tr>
         </table></p>
      }
   }


   /**
    * Builds detail information about the entites.
    */
   def detailsToHtml(extracts : List[MemberExtract], title : String, from : DocTemplateEntity) : NodeSeq = {
      if (extracts.isEmpty)
         NodeSeq.Empty
      else {
         var detailsHtml = Nil:List[Node]
         detailsHtml ++= <a name={title.replace(" ", "_")}><!-- --></a>
         detailsHtml ++= <table border="1" width="100%" cellpadding="3" cellspacing="0" summary="">
            <tr bgcolor="#CCCCFF" class="TableHeadingColor">
               <th align="left" colspan="1">
                  <font size="+2"><b>{title}</b></font>
               </th>
            </tr>
         </table>
         detailsHtml ++= <xml:group>{extracts.map(detailToHtml(_, from))}</xml:group>
         detailsHtml
      }
   }

   def detailToHtml(extract : MemberExtract, from : DocTemplateEntity) : NodeSeq = {
      def signature(extract : MemberExtract) = {
         if (extract.isExecutable)
            extract.allocation + " " + extract.typ + extract.asInstanceOf[MethodExtract].parameters
         else
            extract.allocation + " " + extract.typ
      }
      <a name={extract.name}><!-- --></a>
      <h3>{extract.name}</h3>
      <pre>{signature(extract)}</pre>
      <dl>
         <dd>{entityPresentationUtil.full(extract.entity.comment)}</dd>
         {entityPresentationUtil.tags(extract.entity.comment)}
         {
            val en = extract.entity
            if (en.isInstanceOf[ConstantVal]) {
               val const = en.asInstanceOf[ConstantVal]
               val cvalue = const.value
               val x = en.resultType
            }
         }
      </dl>
   }

  /**
    * Comment of this class.
    */
   def comment(entity : MemberEntity) = {
      <xml:node>{entityPresentationUtil.full(entity.comment)}</xml:node> ++ <hr/>
   }

}