package org.scajadoc.page

import java.nio.channels.Channels
import java.io.{File => JFile, FileOutputStream}
import org.scajadoc.settings
import tools.nsc.doc.model.{Package => ScalaPackage}
import org.scajadoc.util.linkResolver

/**
 * Object which persists all page classes on disk. 
 *
 * @author Filip Rogaczewski
 */
class HtmlPageWriter(val rootPackage : ScalaPackage) {

   /**
    * Creates a file in a page's path.
    */
	private def createFile(page : HtmlPage) : JFile = {
		val link = linkResolver.resolve(page.entity).get.link(rootPackage)
      var path = (settings.outdir.value + JFile.separator + link).replace("/", JFile.separator)
      if (page.entity.isInstanceOf[ScalaPackage] && page.entity.asInstanceOf[ScalaPackage].isRootPackage)
         path += page.file
      if (page.isInstanceOf[PackageSummary])
         path = path.replace(settings.packageFrameFile, page.filename)
      val file = new JFile(path)
      if (!file.getParentFile.exists) {
         file.getParentFile.mkdirs
      }
      if (!file.exists) {
         file.createNewFile
      }
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