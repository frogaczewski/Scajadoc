package org.scajadoc

import org.apache.commons.cli.{CommandLine, BasicParser, Options, Option => CliOption}
import java.io.File
import tools.nsc.io.Directory
import tools.nsc.doc.Settings
import collection.JavaConversions._
import io.Source
import java.net.URL
import collection.mutable.ListBuffer

/**
 * Object with settings for compiler, model factory and front end builder.
 */
object settings extends Settings(msg => error(msg)) {

   /**
    * Output format of files generates by front-end.
    */
   final val outputFormat = ".html"

   /**
    * Name of file containing complete list of packages included in Javadoc.
    */
   final val packageListFile = "package-list"

   final val packageFrameFile = "package-frame"

   /**
    * Scajadoc output directory.
    */
   final val apiOutdir = File.separator + "scajadoc"

   /**
    * Links to external APIs.
    */
   var links : List[String] = Nil:List[String]

   /**
    * Title of this javadoc.
    */
	var javadocTitle : String = _

	def setSourcepath(sourcepath : List[String]) = {
//		this.sourcepath.value.split(File.pathSeparatorChar).flatMap{ p => sourcepath }.toList
      this.sourcepath.value = sourcepath.mkString("", File.pathSeparator, "")
   }

	def setClasspath(classpath : String) = this.classpath.value = classpath

	def setOutdir(outdir : Option[String]) = {
      outdir match {
         case Some(v) => {
            val outDirectory = new File(v + apiOutdir)
            if (!outDirectory.exists)
               outDirectory.mkdir
            this.outdir.value = outDirectory.getAbsolutePath
         }
         case None => throw new IllegalArgumentException("Wrong destination directory")
      }
   }

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
	          projectName : String,
             links : Array[String]) = {
		val settings = new DocSettings
		settings.sourcepath = getSources(new File(sourcepath))
		settings.classpath = classpath.mkString(File.pathSeparator)
		settings.docDestinationDir = getDestinationDir(destination)
		settings.javadocTitle = projectName
      if (links != null)
         settings.links = links.toList
		settings
	}

	/** Builds list of command line options */
	private def buildOptions() : Options = {
		val opt = new Options
		opt.addOption("sourcepath", true, "Specifies the search paths for finding source files (.scala)")
		opt.addOption("d", true, "Specifies the destination directory where javadoc saves the generated HTML files.")
		opt.addOption("classpath", true, "Specifies the paths where ScajaDoc will look for referenced classes (.class files) ")
		opt.addOption("doctitle", true, "Specifies the title to be placed near the top of the overview summary file.")
      val linkOption = new CliOption("link", true, "Creates links to existing javadoc-generated documentation of external referenced classes. As an argument it takes link or links to external javadoc documentations.")
      linkOption.setArgs(CliOption.UNLIMITED_VALUES)
      opt.addOption(linkOption)
		opt
	}

	/** Returns list of source files to compile. */
	def getSources(file : File) : List[String] = {
		(new Directory(file)).deepFiles.filter{ e => (e.extension == "scala" || e.extension == "java")}.map{ _.path }.toList
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
		/** Retrieve path to the scala library using system environmental settings. */
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
               Source.fromURL(new URL(lnk + settings.packageListFile))
               linksBuffer += lnk
            } catch {
               case ex : Exception =>
                  throw new IllegalArgumentException("Error while processing list of links to external APIs. Check you network connection and try again.")
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
         links = processLinks(cl.getOptionValues("link"))
      }
	}

}