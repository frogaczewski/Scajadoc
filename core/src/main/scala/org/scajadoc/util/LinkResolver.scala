package org.scajadoc.util

import collection.mutable
import io.Source
import java.net.URL
import org.scajadoc.settings
import tools.nsc.doc.model._
import org.scajadoc.extractor.entityQueryContainer._

/**
 * Utility class resolving links to documentation for external and
 * internal dependencies.
 *
 * Internal references are classes whose documentation is being generated
 * during a Scajadoc run.
 *
 * External references are classes whose documentation is not being generated
 * during a Scajadoc run. Links in the documentation to those classes are said
 * to be external references or external links. Link option requires
 * that a file named package-list, which is generated by Javadoc (or Scajadoc) tool
 * exist at the URL specified with -link. The package-list file is a simple file that
 * lists the names of packages documented at that location. LinkResolver will not check
 * if documentation for classes from packages enlisted in package-list exists at specified
 * location.
 *
 * @author Filip Rogaczewski
 *
 */
object linkResolver {

   /**
    * Retrieves links specified in package-list files of the external dependencies.
    */
   def getLinksToExternalDependencies() = {
      def getPackageList(link : String) : Source = Source.fromURL(new URL(link + settings.packageListFile))
      val links = new mutable.HashMap[String, String]
      settings.links.foreach(link => {
         getPackageList(link).getLines.foreach(packageName => links += (packageName -> link))
      })
      links
   }

   private lazy val linksToExternalDependencies : mutable.Map[String, String] = getLinksToExternalDependencies

   def resolve(template : Entity) : Option[Link] = {
      /**
       * NoDocTemplate represents classes like scala.Any, scala.ScalaObject, java.lang.Object, etc.
       * Those classes are almost always in external dependencies, therefore external link is created.
       *
       * DocTemplateEntity may represent either internal source class or external dependency.
       */
      template match {
         case p : Package => Some(InternalLink(p))
         case dt : DocTemplateEntity => resolve(dt)
         case nt : NoDocTemplate => externalLink(nt)
         case ntm : NonTemplateMemberEntity => makeNonTemplateLink(ntm)
         case _ => None
      }
   }

   /**
    * Resolves and returns link to the template. For instance if the
    * template passed is java.lang.String the result would be link
    * to http://download.oracle.com/javase/6/docs/api/java/lang/String. 
    */
   private def resolve(template : DocTemplateEntity) : Option[Link] = {
      val internal = isInternal(template)
      if (isInternal(template)) {
         Some(InternalLink(template))
      } else {
         externalLink(template)
      }
   }

   /**
    * Returns a link to the API of the dependency's template.
    */
   private def externalLink(template : TemplateEntity) : Option[Link] = {
      val externalApi = linksToExternalDependencies.get(template.toRoot.reverse.filter(isPackage).map(_.name).mkString("."))
      externalApi match {
         case Some(extApiLink) =>
            Some(ExternalLink(template, extApiLink))
         case None => None
      }
   }

   private def makeNonTemplateLink(ntm : NonTemplateMemberEntity) = {
      val inh = ntm.inheritedFrom
      if (isInternal(ntm))
         Some(InternalLink(ntm))
      else {
         var result : Option[Link] = None
         for (tmp <- ntm.inheritedFrom) {
            linksToExternalDependencies.get(tmp.toRoot.reverse.filter(isPackage).map(_.name).mkString(".")) match {
               case Some(extApiLink) => result = Some(NonTemplateExternalLink(ntm, tmp, extApiLink))
               case None => {}
            }
         }
         result
      }
   }

   /**
    * Returns true if the template was generated during this Scajadoc run.
    */
   private def isInternal(template : DocTemplateEntity) : Boolean = {
      template.inSource match {
         case Some(_) => true
         case None => false
      }
   }


   /**
    * If the non-template entity is not inherited then it is a part of the documented project.
    * If the entity's template is inherited from other templates but the entity is not overriden
    * it is still internal.
    */
   private def isInternal(ntm : NonTemplateMemberEntity) : Boolean = {
      /* if (ntm.inheritedFrom.size > 0) {
         val inhfrom = ntm.inheritedFrom
         val isoverride = !ntm.isOverride
         val inhHead = ntm.inheritedFrom.head
         val inTmpl = ntm.inTemplate
         val rxr = 0
      }   */
      return (ntm.inheritedFrom.size == 0 || (ntm.inheritedFrom.size > 0 && !ntm.isOverride))
   }

}

/**
 * Link to the entity.
 *
 * @author Filip Rogaczewski
 */
abstract class Link(entity : Entity) {

   protected final def tmpLink(entity : Entity) = {
      var builder = new StringBuilder
      builder ++= entity.toRoot.reverse.filter(e => (isPackage(e) || isType(e))).map(_.rawName).mkString("/")
      if (isPackage(entity)) {
         builder ++= "/"
         builder ++= settings.packageFrameFile
      }
      builder ++= settings.outputFormat
      builder
   }

   protected final def memberLink(entity : MemberEntity) = {
      var builder = new StringBuilder
      builder ++= "#"
      builder ++= entity.rawName
      builder
   }

   protected def entityLink(tmpl : Entity, member : Entity) = {
      var builder = new StringBuilder
      builder ++= tmpLink(tmpl)
      if (member.isInstanceOf[MemberEntity]
            && (isField(member.asInstanceOf[MemberEntity])
            || member.asInstanceOf[MemberEntity].isConstructor
            || member.asInstanceOf[MemberEntity].isDef)) {
         builder ++= memberLink(member.asInstanceOf[MemberEntity])
      }
      builder
   }

   /**
    * Returns a relative link from the specified entity.
    */
   def link(from : DocTemplateEntity) : String
}

/**
 * Represenation of the internal link.
 *
 * @author Filip Rogaczewski
 */
case class InternalLink(entity : Entity) extends Link(entity) {

   def link(from : DocTemplateEntity) = {
     /*
      * 1) find a 'way' from to root. if 'from' is in /org/scajadoc
       * way to root is ../../
      */
      val wayToRoot = from.toRoot.filter(isPackage(_)).map(e => "../").mkString
      wayToRoot + entityLink(entity, entity)
   }
}

/**
 * Representation of link to the entity from the external library.
 *
 * @author Filip Rogaczewski
 */
case class ExternalLink(entity : Entity, val api : String) extends Link(entity) {

   def link(from : DocTemplateEntity) = {
      api + entityLink(entity, entity)
   }

}

case class NonTemplateExternalLink(entity : Entity, val tmpl : TemplateEntity, val api : String) extends Link(entity) {

   def link(from : DocTemplateEntity) = {
      api + entityLink(tmpl, entity)
   }

}