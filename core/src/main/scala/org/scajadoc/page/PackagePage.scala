package org.scajadoc.page

import org.scajadoc.settings
import org.scajadoc.util.{entityTreeTraverser, linkResolver}
import org.scajadoc.extractor.entityQueryContainer
import tools.nsc.doc.model.{MemberEntity, DocTemplateEntity}
import xml.{NodeSeq, Node}

/**
 * Generates package-frame.html file.
 *
 * @author Filip Rogaczewski
 */
class PackagePage(val pack : DocTemplateEntity) extends HtmlPage {

   import entityQueryContainer._

   def filename = settings.packageFrameFile

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
      bodyHtml ++= packagePageHtmlUtil.linkToPackageSummary(pack)
      bodyHtml ++= packagePageHtmlUtil.typesToHtml(interfaces, "Interfaces", pack)
      bodyHtml ++= packagePageHtmlUtil.typesToHtml(classes, "Classes", pack)
      bodyHtml ++= packagePageHtmlUtil.typesToHtml(enums, "Enums", pack)
      bodyHtml ++= packagePageHtmlUtil.typesToHtml(exceptions, "Exceptions", pack)
      bodyHtml
   }

}

object packagePageHtmlUtil {

   def linkToPackageSummary(from : DocTemplateEntity) = {
      <font size="+1" class="FrameTitleFont">
         <a href="package-summary.html" target="classFrame">{from.qualifiedName}</a>
      </font>
   }

   def typesToHtml(types : List[DocTemplateEntity], title : String, from : DocTemplateEntity) = {
      if (types.isEmpty)
         NodeSeq.Empty
      else {
         <table>
            <tr>
               <td nowrap="nowrap">
                  <font size="+1" class="FrameHeadingFont">{title}</font>
                  <font class="FrameItemFont">
                     {types.map(t => typeToHtml(t, from))}
                  </font>
               </td>
            </tr>
         </table>
      }
   }

   def typeToHtml(typ : DocTemplateEntity, from : DocTemplateEntity) = {
      linkResolver.resolve(typ) match {
         case Some(l) => <br/><a href={l.link(from)}>{typ.rawName}</a>
         case None => <br/><xml:node>{typ.rawName}</xml:node>
      }
   }
}