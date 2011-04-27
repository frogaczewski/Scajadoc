package org.scajadoc.util

import tools.nsc.doc.model._
import org.scajadoc.extractor.entityQueryContainer
import org.scajadoc.settings

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

   def navigationBarHtml(tmp : DocTemplateEntity, simpleNavigation : Boolean = false,
                              packageSummary : Boolean = false, packageTree : Boolean= false,
                              index : Boolean = false, overview : Boolean = false,
                              deprecated : Boolean = false) = {
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
                     {
                        if (overview)
                           <td bgcolor="#EEEEFF" class="NavBarCell1Rev"><font class="NavBarFont1Rev"><b>Overview</b></font></td>
                        else
                           <td bgcolor="#EEEEFF" class="NavBarCell1"><a href={toRoot + "overview-summary.html"}><font class="NavBarFont1"><b>Overview</b></font></a></td>
                     }
                     {
                        if (packageSummary)
                           <td bgcolor="#FFFFFF" class="NavBarCell1Rev"><font class="NavBarFont1Rev"><b>Package</b></font></td>
                        else if (!tmp.isPackage || packageTree)
                           <td bgcolor="#EEEEFF" class="NavBarCell1"><a href="package-summary.html"><font class="NavBarFont1"><b>Package</b></font></a></td>
                        else
                           <td bgcolor="#FFFFFF" class="NavBarCell1"><font class="NavBarFont1">Package</font></td>
                     }
                     {
                        if (!tmp.isPackage)
                           <td bgcolor="#FFFFFF" class="NavBarCell1Rev"><font class="NavBarFont1Rev"><b>Class</b></font></td>
                        else
                           <td bgcolor="#FFFFFF" class="NavBarCell1"><font class="NavBarFont1">Class</font></td>

                     }
                     <!--<td bgcolor="#EEEEFF" class="NavBarCell1"><a href="class-use/.html"><font class="NavBarFont1"><b>Use</b></font></a></td> -->
                     {
                        if (packageTree)
                           <td bgcolor="#FFFFFF" class="NavBarCell1Rev"><font class="NavBarFont1Rev"><b>Tree</b></font></td>
                        else
                           <td bgcolor="#EEEEFF" class="NavBarCell1"><a href="package-tree.html"><font class="NavBarFont1"><b>Tree</b></font></a>&nbsp;</td>
                     }
                     {
                        if (deprecated)
                           <td bgcolor="#FFFFFF" class="NavBarCell1Rev"><font class="NavBarFont1Rev"><b>Deprecated</b></font></td>
                        else
                           <td bgcolor="#EEEEFF" class="NavBarCell1"><a href={toRoot + "deprecated-list.html"}><font class="NavBarFont1"><b>Deprecated</b></font></a></td>
                     }
                     {
                        if (index)
                           <td bgcolor="#EEEEFF" class="NavBarCell1Rev"><font class="NavBarFont1Rev"><b>Index</b></font></td>
                        else
                           <td bgcolor="#EEEEFF" class="NavBarCell1"><a href={toRoot + "index-all.html"}><font class="NavBarFont1"><b>Index</b></font></a></td>
                     }
                     <td bgcolor="#EEEEFF" class="NavBarCell1"><a href={toRoot + "help-doc.html"}><font class="NavBarFont1"><b>Help</b></font></a></td>
                  </tr>
               </table>
            </td>
            <td align="right" valign="top" rowspan="3">
               <em></em>
            </td>
         </tr>
         <tr>
            {
               if (!simpleNavigation)
                  <td bgcolor="white" class="NavBarCell2">
                     <font size="-2">
                        {
                           if (tmp.isPackage)
                              typeSlider.prevPackage(tmp) ++ typeSlider.nextPackage(tmp)
                           else
                              typeSlider.prevClass(tmp) ++ typeSlider.nextClass(tmp)
                        }
                     </font>
                  </td>
               else
                  <td bgcolor="white" class="NavBarCell2">&nbsp;</td>
            }
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
         {
            if (!tmp.isPackage)
               <tr>
                  <td valign="top" class="NavBarCell3">
                     <font size="-2">SUMMARY:&nbsp;<a href="#nested_summary">NESTED</a>&nbsp;|&nbsp;<a href="#field_summary">FIELD</a>&nbsp;|&nbsp;<a href="#constructor_summary">CONSTR</a>&nbsp;|&nbsp;<a href="#method_summary">METHOD</a></font>
                  </td>
                  </tr>
         }
      </table>
      <a name="skip-navbar_top"></a>
   }

}


object typeSlider {

   var types : List[DocTemplateEntity] = _
   var revTypes : List[DocTemplateEntity] = _

   var packages : List[DocTemplateEntity] = _
   var revPackages : List[DocTemplateEntity] = _

   def init(types : List[DocTemplateEntity], packages : List[DocTemplateEntity]) = {
      this.types = types.sortBy(_.rawName)
      this.revTypes = this.types.reverse
      this.packages = packages.sortBy(_.rawName)
      this.revPackages = this.packages.reverse
   }

   def nextClass(from : DocTemplateEntity) : xml.NodeSeq = {
      val next = nextDoc(from, types.iterator)
      next match {
         case Some(nxt) => <xml:group>&nbsp;<a href={linkResolver.resolve(nxt).get.link(from)}><b>NEXT CLASS</b></a></xml:group>
         case None => <xml:node>&nbsp;NEXT CLASS</xml:node>
      }
   }

   def prevClass(from : DocTemplateEntity) : xml.NodeSeq = {
      val prev = nextDoc(from, revTypes.iterator)
      prev match {
         case Some(prv) => <xml:group><a href={linkResolver.resolve(prv).get.link(from)}><b>PREV CLASS</b></a>&nbsp;</xml:group>
         case None => <xml:node>PREV CLASS&nbsp;</xml:node>
      }
   }

   def prevPackage(from : DocTemplateEntity) : xml.NodeSeq = {
      val prev = nextDoc(from, revPackages.iterator)
      prev match {
         case Some(prv) => <xml:group><a href={linkResolver.resolve(prv).get.link(from).replace(settings.packageFrameFile, "package-summary")}><b>PREV PACKAGE</b></a>&nbsp;</xml:group>
         case None => <xml:node>PREV PACKAGE&nbsp;</xml:node>
      }
   }

   def nextPackage(from : DocTemplateEntity) : xml.NodeSeq = {
      val next = nextDoc(from, packages.iterator)
      next match {
         case Some(nxt) => <xml:group><a href={linkResolver.resolve(nxt).get.link(from).replace(settings.packageFrameFile, "package-summary")}><b>NEXT PACKAGE</b></a>&nbsp;</xml:group>
         case None => <xml:node>NEXT PACKAGE&nbsp;</xml:node>
      }
   }

   private def nextDoc(from : DocTemplateEntity, it : Iterator[DocTemplateEntity]) : Option[DocTemplateEntity] = {
      var found = false
      while (it.hasNext && !found) {
         val next = it.next
         if (next == from && it.hasNext)
            found = true
      }
      if (found)
         Some(it.next)
      else
         None
   }

}
