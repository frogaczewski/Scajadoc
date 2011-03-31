package org.scajadoc

import org.apache.commons.cli.{CommandLine, BasicParser, Options, Option => CliOption}
import java.io.File
import tools.nsc.io.Directory
import tools.nsc.doc.Settings
import collection.JavaConversions._
import io.Source
import java.net.URL
import collection.mutable.ListBuffer

object settings extends Settings(msg => error(msg)) {

   final val OUTPUT_FORMAT = ".html"

   final val PACKAGE_LIST_FILE = "package-list"

   var links : List[String] = Nil:List[String]

	var javadocTitle : String = _

	def setSourcepath(sourcepath : List[String]) =
		this.sourcepath.value.split(File.pathSeparatorChar).flatMap{ p => sourcepath }.toList

	def setClasspath(classpath : String) = this.classpath.value = classpath

	def setOutdir(outdir : Option[String]) = this.outdir.value = outdir.getOrElse(
			throw new Exception("Wrong destination directory"))

	def setJavadocTitle(title : String) = this.javadocTitle = title

   def setLinks(lnks : List[String]) = this.links = lnks

}

/**
 * Encapsulation of Scajadoc settings.
 *
 * @author Filip Rogaczewski
 */
object DocSettings {

	def apply(args : Array[String]) : DocSettings = {
		val settings = new DocSettings
		settings.processOptions(buildOptions, args)
		settings
	}

	def apply(sourcepath : String,
	          destination : String,
	          classpath : java.util.Set[String],
	          projectName : String) = {
		val settings = new DocSettings
		settings.sourcepath = getSources(new File(sourcepath))
		settings.classpath = classpath.mkString(File.pathSeparator)
		settings.docDestinationDir = getDestinationDir(destination)
		settings.javadocTitle = projectName
		settings
	}

	/** Builds list of command line options */
	private def buildOptions() : Options = {
		val opt = new Options
		opt.addOption("sourcepath", true, "Specifies the search paths for finding source files (.scala)")
		opt.addOption("d", true, "Specifies the destination directory where javadoc saves the generated HTML files.")
		opt.addOption("classpath", true, "Specifies the paths where javadoc will look for referenced classes (.class files) ")
		opt.addOption("doctitle", true, "Specifies the title to be placed near the top of the overview summary file.")
      val linkOption = new CliOption("link", true, "Creates links to existing javadoc-generated documentation of external referenced classes. As an argument it takes link or links to external javadoc documentations.")
      linkOption.setArgs(CliOption.UNLIMITED_VALUES)
      opt.addOption(linkOption)
		opt
	}

	/** Returns list of source files to compile. */
	def getSources(file : File) : List[String] = {
		(new Directory(file)).deepFiles.filter{ _.extension == "scala" }.map{ _.path }.toList
	}

	def getDestinationDir(dir : String) : Option[String] = {
		val directory = new File(dir)
		if (directory.exists && directory.isDirectory)
			Some(dir)
		else if (!directory.exists) {
			directory.mkdir
			Some(dir)
		} else
			None
	}
}


class DocSettings {

	/** List of source files to document. */
	var sourcepath : List[String] = _

	/** Compiler classpath. */
	var classpath : String = _

	/** Target destination. */
	var docDestinationDir : Option[String] = None

	var javadocTitle : String = _

   var links : List[String] = Nil:List[String]

	/**
	 * Processes command line arguments and finds options values.
	 */
	private def processOptions(options : Options, args : Array[String]) = {
		/** Retrieve path to scala library using system environmental settings. */
		def scalaClasspath() : List[String] = {
			val scalaLib = System.getenv("SCALA_HOME") + "\\lib"
			List(
				new File(scalaLib, "scala-library.jar").getAbsolutePath,
				new File(scalaLib, "scala-compiler.jar").getAbsolutePath
			)
		}

		/** Returns classpath extended by path to scala library */
		def makeClasspath(_classpath : String) : String = {
			val classpath = scalaClasspath ::: List(_classpath)
			classpath.mkString(File.pathSeparator)
		}

      /**
       * Validates links and return list of well-formed links.
       */
      def processLinks(links : Array[String]) : List[String] = {
         val linksBuffer = new ListBuffer[String]
         for (link <- links) {
            var lnk = link.trim
            if (lnk.last != '/')
               lnk = lnk + '/'
            try {
               Source.fromURL(new URL(lnk + settings.PACKAGE_LIST_FILE))
               linksBuffer += lnk
            } catch {
               case ex : Exception => throw new IllegalArgumentException(ex.getCause)
            }
         }
         linksBuffer.toList
      }

		val cl : CommandLine = new BasicParser().parse(options, args)
		if (cl.hasOption("sourcepath")) {
			sourcepath = DocSettings.getSources(new File(cl.getOptionValue("sourcepath")))
		}
		if (cl.hasOption("d")) {
			docDestinationDir = DocSettings.getDestinationDir(cl.getOptionValue("d"))
		}
		if (cl.hasOption("classpath")) {
			classpath = makeClasspath(cl.getOptionValue("classpath"))
		} else {
			classpath = scalaClasspath.mkString(File.pathSeparator)
		}
		if (cl.hasOption("doctitle")) {
			javadocTitle = cl.getOptionValue("doctitle")
		} else {
			javadocTitle = "API"
		}
      if (cl.hasOption("link")) {
         val xxx = cl.getOptionValues("link")
         links = processLinks(cl.getOptionValues("link"))
      }
	}

}