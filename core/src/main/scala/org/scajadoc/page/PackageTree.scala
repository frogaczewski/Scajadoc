package org.scajadoc.page

import tools.nsc.doc.model.DocTemplateEntity
import org.scajadoc.extractor.entityQueryContainer
import org.scajadoc.util.entityTreeTraverser
import collection.immutable.List._
import xml.Node

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

   def body = {
      <xml:node>node</xml:node>
   }
}