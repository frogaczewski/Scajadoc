package org.scajadoc.page

import tools.nsc.doc.model.DocTemplateEntity
import org.scajadoc.settings
import collection.immutable.List._
import org.scajadoc.extractor._
import xml.{NodeSeq, Node}
import org.scajadoc.util.{NavigationBarHtmlUtil, entityPresentationUtil, linkResolver, entityTreeTraverser}

/**
 * Generates package-summary.html file.
 *
 * @author Filip Rogaczewski
 */
class PackageSummary(val pack : DocTemplateEntity) extends HtmlPage {

   import entityQueryContainer._

   def filename = "package-summary"

   def entity = pack

   def pageTitle = pack.qualifiedName

   def headers = ""

   def body = {
      val types = entityTreeTraverser.collect(pack, isType).map(_.asInstanceOf[DocTemplateEntity])
      val interfaces = types.filter(isInterface)
      val enums = types.filter(isEnumeration)
      val exceptions = types.filter(isException)
      val classes = types -- interfaces -- enums -- exceptions
      var bodyHtml = Nil:List[Node]
      bodyHtml ++= packageSummaryHtmlUtil.navigationBarHtml(pack)
      bodyHtml ++= packageSummaryHtmlUtil.packageHeaderHtml(pack)
      bodyHtml ++= packageSummaryHtmlUtil.typesToHtml(interfaces, "Interfaces", pack)
      bodyHtml ++= packageSummaryHtmlUtil.typesToHtml(classes, "Classes", pack)
      bodyHtml ++= packageSummaryHtmlUtil.typesToHtml(enums, "Enums", pack)
      bodyHtml ++= packageSummaryHtmlUtil.typesToHtml(exceptions, "Exceptions", pack)
      bodyHtml ++= packageSummaryHtmlUtil.packageDescription(pack)
      bodyHtml
   }
}

/**
 * Html utils for package-summary.html.
 *
 * @author Filip Rogaczewski
 */
object packageSummaryHtmlUtil extends NavigationBarHtmlUtil {

   val typeExtractor = new TypeExtractor

   def extractSummary(extract : TypeExtract, from : DocTemplateEntity) = {
      <tr bgcolor="white" class="TableRowColor">
         <td align="right" valign="top" width="15%">{
            linkResolver.resolve(extract.entity) match {
               case Some(l) => <a href={l.link(from)} title={extract.typ + " in " + extract.inPackage.qualifiedName}>{extract.name}</a>
               case None => <xml:node>{extract.name}</xml:node>
            }
         }</td>
         <td>
            {entityPresentationUtil.short(extract.entity.comment)}
         </td>
      </tr>
   }

   def typesToHtml(types : List[DocTemplateEntity], title : String, from : DocTemplateEntity) = {
      if (types.isEmpty)
         NodeSeq.Empty
      else {
         var summaryHtml = Nil:List[Node]
         summaryHtml ++= <!-- =========== {summary} =========== -->
         summaryHtml ++= <a name={title.replace(" ", "_")}><!-- --></a>
         summaryHtml ++= <table border="1" width="100%" cellpadding="3" cellspacing="0" summary="">
            <tr bgcolor="#CCCCFF" class="TableHeadingColor">
               <th align="left" colspan="2"><font size="+2"><b>{title}</b></font></th>
            </tr> {types.map(e => extractSummary(typeExtractor.extract(e).get, from))} </table>
         summaryHtml ++= <br/>
         summaryHtml
      }
   }

   def packageHeaderHtml(pack : DocTemplateEntity) = {
      <hr /><h2>Package {pack.qualifiedName}</h2>
         <p>{entityPresentationUtil.short(pack.comment)}</p>
         <p><b>See:</b><br/>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#package_description"><b>Description</b></a>
         </p>
   }

   def packageDescription(pack : DocTemplateEntity) = {
      <p>
         <a name="package_description"><!-- --></a>
         <h2>Package {pack.qualifiedName}</h2>
      </p>
      <p>{entityPresentationUtil.full(pack.comment)}</p>
   }
}