package org.scajadoc.frontend

import page.HtmlPage
import java.nio.channels.Channels
import java.io.{File => JFile, FileOutputStream}
import org.scajadoc.settings

/**
 * Object which persists all page classes on disk. 
 *
 * @author Filip Rogaczewski
 */
object htmlPageWriter {

	def createFile(page : HtmlPage) : JFile = {
		val classpath = classpathCache(page.entity)
		val dir = new JFile(settings.outdir.value + JFile.separator + classpath.docPackageClasspath)
		if (!dir.exists)
			dir.mkdirs
		val file = new JFile(dir, page.file)
		if (!file.exists)
			file.createNewFile
		file
	}

	/**
	 * Writes the html page to location relevant to the page's canonical name. 
	 */
	@throws(classOf[Exception])
	def write(page : HtmlPage) = {
		val fos = new FileOutputStream(createFile(page))
		val writer = Channels.newWriter(fos.getChannel, page.encoding)
		writer.write("<?xml version='1.0' encoding='" + page.encoding + "'?>\n")
		writer.write(xml.Xhtml.toXhtml(page.html))
		writer.close
		fos.close
	}

}