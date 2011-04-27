package org.scajadoc.util

import tools.nsc.doc.model.{TemplateEntity, DocTemplateEntity, Entity}
import xml.NodeSeq

/**
 * Html utils for links.
 *
 * @author Filip Rogaczewski
 */
trait LinkHtmlUtil {

   private def link(ent : Entity, from : DocTemplateEntity) = linkResolver.resolve(ent) match {
      case Some(l) => <a href={l.link(from)}>{ent.rawName}</a>
      case None => <xml:node>{ent.rawName}</xml:node>
   }

   /**
    * Returns link in the fully qualified form.
    */
   def canonicalLink(e : Entity, from : DocTemplateEntity) : NodeSeq = {
      if (!e.inTemplate.isRootPackage)
         <xml:node>{e.inTemplate.qualifiedName+"."}</xml:node>++{link(e, from)}
      else
         link(e, from)
   }

   /**
    * Returns link in a simple form.
    */
   def simpleLink(e : Entity, from : DocTemplateEntity) = {
      link(e, from)
   }

}