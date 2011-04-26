package org.scajadoc.util

import tools.nsc.doc.model.DocTemplateEntity
import org.scajadoc.extractor.entityQueryContainer

/**
 * Trait for a navigation bar generation.
 *
 * @author Filip Rogaczewski
 */
trait NavigationBarHtmlUtil {

   import entityQueryContainer._

   /**
    * Returns way to root.
    */
   private def wayToRoot(tmp : DocTemplateEntity) : String = {
      tmp.toRoot.filter(isPackage(_)).map(e => "../").mkString
   }

   def navigationBarHtml(tmp : DocTemplateEntity) = {
      def rootPackage = tmp.toRoot.filter(isRootPackage).head
      val toRoot = wayToRoot(tmp)
      <!-- ========= START OF TOP NAVBAR ======= -->
      <a name="navbar_top"><!-- --></a>
      <a href="#skip-navbar_top" title="Skip navigation links"></a>
      <table border="0" width="100%" cellpadding="1" cellspacing="0" summary="">
         <tr>
            <td colspan="2" bgcolor="#EEEEFF" class="NavBarCell1">
               <a name="navbar_top_firstrow"><!-- --></a>
               <table border="0" cellpadding="0" cellspacing="3" summary="">
                  <tr align="center" valign="top">
                     <td bgcolor="#EEEEFF" class="NavBarCell1"><a href={toRoot + "overview-summary.html"}><font class="NavBarFont1"><b>Overview</b></font></a></td>
                     <td bgcolor="#EEEEFF" class="NavBarCell1"><a href="package-summary.html"><font class="NavBarFont1"><b>Package</b></font></a></td>
                     <td bgcolor="#FFFFFF" class="NavBarCell1Rev"><font class="NavBarFont1Rev"><b>Class</b></font></td>
                     <!--<td bgcolor="#EEEEFF" class="NavBarCell1"><a href="class-use/.html"><font class="NavBarFont1"><b>Use</b></font></a></td> -->
                     <td bgcolor="#EEEEFF" class="NavBarCell1"><a href="package-tree.html"><font class="NavBarFont1"><b>Tree</b></font></a>&nbsp;</td>
                     <td bgcolor="#EEEEFF" class="NavBarCell1"><a href={toRoot + "deprecated-list.html"}><font class="NavBarFont1"><b>Deprecated</b></font></a></td>
                     <td bgcolor="#EEEEFF" class="NavBarCell1"><a href={toRoot + "index-all.html"}><font class="NavBarFont1"><b>Index</b></font></a></td>
                     <td bgcolor="#EEEEFF" class="NavBarCell1"><a href={toRoot + "help-doc.html"}><font class="NavBarFont1"><b>Help</b></font></a></td>
                  </tr>
               </table>
            </td>
            <td align="right" valign="top" rowspan="3">
               <em></em>
            </td>
         </tr>
         <tr>
            <td bgcolor="white" class="NavBarCell2">
               <font size="-2">
                  {typeSlider.prevClass(tmp)}
                  {typeSlider.nextClass(tmp)}
               </font>
            </td>
            <td bgcolor="white" class="NavBarCell2">
               <font size="-2">
                  <a href={toRoot + "index.html?" + linkResolver.resolve(tmp).get.link(rootPackage)} target="_top"><b>FRAMES</b></a>&nbsp;
                  &nbsp;<a href={tmp.rawName + ".html"} target="_top"><b>NO FRAMES</b></a>&nbsp;
                  <noscript>
                  <a href={toRoot + "allclasses-noframe.html"}><b>All Classes</b></a>
                  </noscript>
               </font>
            </td>
         </tr>
         <tr>
            <td valign="top" class="NavBarCell3">
               <font size="-2">SUMMARY:&nbsp;<a href="#nested_summary">NESTED</a>&nbsp;|&nbsp;<a href="#field_summary">FIELD</a>&nbsp;|&nbsp;<a href="#constructor_summary">CONSTR</a>&nbsp;|&nbsp;<a href="#method_summary">METHOD</a></font>
            </td>
         </tr>
      </table>
      <a name="skip-navbar_top"></a>
   }

}


object typeSlider {

   var types : List[DocTemplateEntity] = _
   var revTypes : List[DocTemplateEntity] = _

   def init(types : List[DocTemplateEntity]) = {
      this.types = types.sortBy(_.rawName)
      this.revTypes = this.types.reverse
   }

   def nextClass(from : DocTemplateEntity) : xml.NodeSeq = {
      val it = types.iterator
      while (it.hasNext) {
         val next = it.next
         if (next == from && it.hasNext) {
            return <xml:group>&nbsp;<a href={linkResolver.resolve(it.next).get.link(from)}><b>NEXT CLASS</b></a></xml:group>
         }
      }
      <xml:node>&nbsp;NEXT CLASS</xml:node>
   }

   def prevClass(from : DocTemplateEntity) : xml.NodeSeq = {
      val it = revTypes.iterator
      while (it.hasNext) {
         val next = it.next
         if (next == from && it.hasNext) {
            return <xml:group><a href={linkResolver.resolve(it.next).get.link(from)}><b>PREV CLASS</b></a>&nbsp;</xml:group>
         }
      }
      <xml:node>PREV CLASS&nbsp;</xml:node>
   }
}
