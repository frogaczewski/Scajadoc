package org.scajadoc.page

import tools.nsc.doc.model.DocTemplateEntity
import org.scajadoc.extractor.entityQueryContainer
import org.scajadoc.settings
import org.scajadoc.util.{linkResolver, entityPresentationUtil, entityTreeTraverser}

/**
 * Generates welcome page - overview-summary.html.
 *
 * @author Filip Rogaczewski
 */
class OverviewSummaryPage(val template : DocTemplateEntity) extends HtmlPage {

   import entityQueryContainer._

   def filename = "overview-summary"

   def entity = template

   def pageTitle = "Overview"

   def headers = ""

   def body = {
      val packages = entityTreeTraverser.collect(template, isPackage).sortBy(m => m.qualifiedName).map(_.asInstanceOf[DocTemplateEntity])
      <hr /><center><h1>{settings.javadocTitle} Documentation</h1></center>
      <p>This document is the API specification for {settings.javadocTitle}</p>
      <p>
         <table border="1" width="100%" cellpadding="3" cellspacing="0" summary="">
            <tr bgcolor="#CCCCFF" class="TableHeadingColor">
               <th align="left" colspan="2">
                  <font size="+2"><b>Packages</b></font>
               </th>
            </tr>
            {packages.filter(isDocumentablePackage(_)).map(overviewSummaryHtmlUtil.packageToHtml(_, template))}
         </table>
      </p>
   }
}

object overviewSummaryHtmlUtil {

   def packageToHtml(pack : DocTemplateEntity, from : DocTemplateEntity) = {
      val comp = pack.companion
      <tr bgcolor="white" class="TableRowColor">
         <td width="20%"><b>{
            linkResolver.resolve(pack) match {
               case Some(l) =>
                  <a href={l.link(from).replace(settings.packageFrameFile, "package-summary")}>
                     {pack.qualifiedName}</a>
               case None => <xml:group>{pack.qualifiedName}</xml:group>
            }
         }</b></td>
         <td>{entityPresentationUtil.short(pack.comment)}</td>
      </tr>
   }

}