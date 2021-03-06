package org.scajadoc.page

import tools.nsc.doc.model.{Package => ScalaPackage}

/**
 * Generate allclasses-noframe.html.
 *
 * @author Filip Rogaczewski
 */
class AllClassesNoFramePage(rootPackage : ScalaPackage) extends AbstractAllClassesPage(rootPackage) {

	/**
	 * Name of the output file.
	 */
	def filename = "allclasses-noframe"

	def headers = ""

	def targetFrame = "_self"

}