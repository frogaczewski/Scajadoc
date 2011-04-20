package org.scajadoc.page

import org.scajadoc.extractor.entityQueryContainer
import org.scajadoc.util.{linkResolver, entityTreeTraverser}
import xml.{Node, NodeSeq}
import tools.nsc.doc.model.{DocTemplateEntity}

/**
 * Generates overview-frame.html file.
 *
 * @author Filip Rogaczewski
 */
class OverviewFramePage(val template : DocTemplateEntity) extends HtmlPage {

   import entityQueryContainer._

   def filename = "overview-frame"

   def entity = template

   def pageTitle = "Overview"

   def headers = ""

   def body = {
      def isDocumentable(pack : DocTemplateEntity) = {
         pack.members.filter(!isPackage(_)).size != 0
      }
      val packages = entityTreeTraverser.collect(template, isPackage).sortBy(m => m.qualifiedName).map(_.asInstanceOf[DocTemplateEntity])
      <table border="0" width="100%" summary="">
         <tr><td><font class="FrameItemFont"><a href="allclasses-frame.html" target="packageFrame">All Classes</a></font>
            <p><font size="+1" class="FrameHeadingFont">Packages</font><br/> {
               packages.filter(isDocumentable(_)).map(overviewHtmlUtil.packageToHtml(_, template))
            }
            </p>
         </td></tr>
      </table>
   }

}

object overviewHtmlUtil {

   def packageToHtml(pack : DocTemplateEntity, from : DocTemplateEntity) : NodeSeq = {
      linkResolver.resolve(pack) match {
         case Some(l) => <a href={l.link(from)}>{pack.qualifiedName}</a><br/>
         case None => <xml:node>{pack.qualifiedName}</xml:node><br/>
      }
   }

}