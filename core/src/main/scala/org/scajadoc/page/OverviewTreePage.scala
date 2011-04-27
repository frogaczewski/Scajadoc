package org.scajadoc.page

import tools.nsc.doc.model.DocTemplateEntity

/**
 * Generates overview-tree.html.
 *
 * @author Filip Rogaczewski
 */
class OverviewTreePage(pack : DocTemplateEntity) extends PackageTree(pack) {
   override def filename = "overview-tree"
}