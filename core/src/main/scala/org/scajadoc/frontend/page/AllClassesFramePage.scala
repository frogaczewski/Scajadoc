package org.scajadoc.frontend.page

import tools.nsc.doc.model.{Package => ScalaPackage}

/**
 * Generate allclasses-frame.html.
 *
 * @author Filip Rogaczewski
 */
class AllClassesFramePage(rootPackage : ScalaPackage) extends AbstractAllClassesPage(rootPackage) {

	/**
	 * Name of the output file.
	 */
	def filename = "allclasses-frame"

	def headers = ""

	def targetFrame = "classFrame"

}