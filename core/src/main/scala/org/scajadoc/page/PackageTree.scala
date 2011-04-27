package org.scajadoc.page

import collection.immutable.List._
import xml.{NodeSeq, Node}
import org.scajadoc.extractor.{TypeExtract, TypeExtractor, entityQueryContainer}
import tools.nsc.doc.model.{Entity, TemplateEntity, DocTemplateEntity}
import org.scajadoc.util.{LinkHtmlUtil, linkResolver, NavigationBarHtmlUtil, entityTreeTraverser}

/**
 * Generates package-tree.html.
 *
 * @author Filip Rogaczewski
 */
class PackageTree(val pack : DocTemplateEntity) extends HtmlPage {

   import entityQueryContainer._

   def filename = "package-tree"

   def entity = pack

   def pageTitle = pack.qualifiedName

   def headers = ""

   private final val typeExtractor = new TypeExtractor

   def body = {
      val types = entityTreeTraverser.collect(pack, isType).map(_.asInstanceOf[DocTemplateEntity])
      val interfaces = types.filter(isInterface)
      val enums = types.filter(isEnumeration)
      val exceptions = types.filter(isException)
      val classes = types -- interfaces -- enums -- exceptions
      val superClasses = (e : TypeExtract) => e.superClasses
      val superInterfaces = (e : TypeExtract) => e.interfaces

      var body = Nil:List[Node]
      body ++= packageTreeHtmlUtil.navigationBarHtml(pack, packageTree = true, simpleNavigation = true)
      body ++= <hr/>
      body ++= header
      body ++= packageTreeHtmlUtil.extractsToTreeHtml(pack, classes.map(typeExtractor.extract(_)),
         "class", superClasses)
      body ++= packageTreeHtmlUtil.extractsToTreeHtml(pack, interfaces.map(typeExtractor.extract(_)),
         "interface", superInterfaces)
      body ++= packageTreeHtmlUtil.extractsToTreeHtml(pack, enums.map(typeExtractor.extract(_)),
         "Enum", superClasses)
      body ++= packageTreeHtmlUtil.extractsToTreeHtml(pack, exceptions.map(typeExtractor.extract(_)),
         "Exception", superClasses)
      body
   }

   def header() = {
      def title = {
         if (!pack.isRootPackage)
            "Hierarchy For Package " + pack.qualifiedName
         else
            "Hierarchy For All Packages"
      }
      <center><h2>{title}</h2></center>
   }
}

/**
 * Html utils for package-tree.html.
 *
 * @author Filip Rogaczewski
 */
object packageTreeHtmlUtil extends NavigationBarHtmlUtil with LinkHtmlUtil {

   private def entityTreeToHtml(root : TreeElement, from : DocTemplateEntity) : NodeSeq = {
      <ul>
         <li type="circle">{canonicalLink(root.entity, from)}</li>
         {root.children.map(entityTreeToHtml(_, from))}
      </ul>
   }

   def extractsToTreeHtml(pack : DocTemplateEntity, extracts : List[Option[TypeExtract]],
               title : String, treeParentQualifier : TypeExtract => List[TemplateEntity]) : NodeSeq = {
      val root = new TreeElement(pack)
      def buildTree(extract : TypeExtract) = {
         root.addChildren(treeParentQualifier(extract) ::: List(extract.entity.asInstanceOf[TemplateEntity]))
      }
      extracts.foreach(extract => {
         if (extract.isDefined)
            buildTree(extract.get)
      })
      val children = root.children.toList
      if (!children.isEmpty)
         <h2>{title.capitalize} Hierarchy</h2> ++ <xml:group>{children.map(entityTreeToHtml(_, pack))}</xml:group>
      else
         NodeSeq.Empty
   }
}

/**
 * Element of the types tree.
 *
 * @author Filip Rogaczewski
 */
class TreeElement(val entity : TemplateEntity) {

   val children = collection.mutable.Set.empty[TreeElement]

   def addChildren(entities : List[TemplateEntity]) : Unit = {
      var child : TreeElement = null
      children.find(_.entity.qualifiedName == entities.head.qualifiedName) match {
         case Some(c) => child = c
         case None => child = new TreeElement(entities.head)
      }
      if (!entities.tail.isEmpty)
         child.addChildren(entities.tail)
      children.add(child)
   }
}