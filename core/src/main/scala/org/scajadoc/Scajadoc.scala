package org.scajadoc

import tools.nsc.reporters.ConsoleReporter
import tools.nsc.doc.DocFactory

/**
 * Entry point for building Javadoc from Scala sources. 
 *
 * @author Filip Rogaczewski
 */
object Scajadoc {

	def main(args : Array[String]) : Unit = {
		val docSettings = DocSettings(args)
		process(docSettings)
	}

	def build(sourcepath : String,
	          destination : String,
	          classpath : java.util.Set[String],
	          projectName : String,
             links : Array[String]) = {
		val docSettings = DocSettings(sourcepath, destination, classpath, projectName, links)
		process(docSettings)
	}

	private def process(docSettings : DocSettings) = {
		settings.setClasspath(docSettings.classpath)
		settings.setOutdir(docSettings.docDestinationDir)
		settings.setSourcepath(docSettings.sourcepath)
		settings.setJavadocTitle(docSettings.javadocTitle)
      settings.setLinks(docSettings.links)
		val reporter = new ConsoleReporter(settings) {
			override def hasErrors = false
		}
		val docProcessor = new DocFactory(reporter, settings)
		val universe = docProcessor.universe(docSettings.sourcepath)
		universe.foreach(new FrontEndBuilder(_).build)
	}
}