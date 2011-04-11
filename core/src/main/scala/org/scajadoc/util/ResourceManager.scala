package org.scajadoc.util

import org.scajadoc.settings
import java.io.{FileOutputStream, File}
import java.nio.channels.Channels
import tools.nsc.io.Streamable

/**
 * Object handling the operations on Scajadoc resources, such as images,
 * cascade-stylesheets, etc.
 *
 * @author Filip Rogaczewski
 */
object resourceManager {

   private final val outputDirectory = {
      val dir = new File(settings.outdir.value + File.separator + "resources")
      if (!dir.exists)
         dir.mkdir
      dir
   }

   def copyResources() = {
      def copyFile(filename : String) {
         val outputFile = new File(outputDirectory, filename)
         val input = new Streamable.Bytes {
            val inputStream = getClass.getResourceAsStream(filename)
            assert(inputStream != null)
         }.toByteArray
         val fos = new FileOutputStream(outputFile)
         fos.write(input, 0, input.length)
         fos.close
      }
      copyFile("/inherit.gif")
   }

   /**
    * Returns absolute path to inherit.gif.
    */
   def inheritGif() = {
      new File(outputDirectory, "/inherit.gif").getCanonicalPath
   }

}