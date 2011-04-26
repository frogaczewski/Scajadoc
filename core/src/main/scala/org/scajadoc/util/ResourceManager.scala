package org.scajadoc.util

import org.scajadoc.settings
import java.io.{FileOutputStream, File}
import tools.nsc.io.Streamable
import java.text.SimpleDateFormat
import java.util.Date
import io.Source

/**
 * Object handling the operations on Scajadoc resources, such as images,
 * cascade-stylesheets, etc.
 *
 * @author Filip Rogaczewski
 */
object resourceManager {

   private final implicit val outputDirectory = {
      val dir = new File(settings.outdir.value + File.separator + "resources")
      if (!dir.exists)
         dir.mkdir
      dir
   }

   def copyResources() = {
      def writeTemplate(filename : String, outdir : File) = {
         val input = Source.fromInputStream(getClass.getResourceAsStream(filename)).mkString
         val output = input
               .replace("$scajadoc-generation-time", new SimpleDateFormat("dd.MM.yyyy").format(new Date()))
               .replace("$scajadoc-title", settings.javadocTitle)
         val bytes = output.getBytes("UTF-8")
         val fos = new FileOutputStream(new File(outdir, filename))
         fos.write(bytes, 0, bytes.length)
         fos.close
      }
      def copyFile(filename : String)(implicit outputDirectory : File) = {
         val outputFile = new File(outputDirectory, filename)
         val input = new Streamable.Bytes {
            val inputStream = getClass.getResourceAsStream(filename)
            assert(inputStream != null)
         }.toByteArray
         val fos = new FileOutputStream(outputFile)
         fos.write(input, 0, input.length)
         fos.close
      }
      val outdir = new File(settings.outdir.value)
      copyFile("/inherit.gif")
      copyFile("/stylesheet.css")(outdir)
      writeTemplate("/index.html", outdir)
      writeTemplate("/help-doc.html", outdir)
   }

   /**
    * Returns absolute path to inherit.gif.
    */
   def inheritGif() = {
      new File(outputDirectory, "/inherit.gif").getCanonicalPath
   }

}