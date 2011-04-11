package org.scajadoc

import org.junit.runner.RunWith
import org.specs.runner.{JUnit4, JUnitSuiteRunner}
import org.specs.Specification
import org.specs.mock.Mockito
import util.resourceManager
import io.Source
import java.io.File


/**
 * Unit tests for resource manager.
 *
 * @author Filip Rogaczewski
 */
@RunWith(classOf[JUnitSuiteRunner])
class ResourceManagerRunAsTest extends JUnit4(resourceManagerTest)

object resourceManagerTest extends Specification("Specification of resource manager") with Mockito {

   private def cleanFiles(dir : String) : Unit = {
      val testDir = new File(dir)
      for (file <- testDir.list) {
         val f = new File(file)
         if (f.isDirectory) {
            cleanFiles(f.getAbsolutePath)
         }
         f.delete
      }
   }

   "Resource manager copy resource" should {
      doBefore(settings.outdir.value = "C:\\Users\\Filip\\Desktop\\PracaDyplomowa\\Testing\\tests")
      doAfter(cleanFiles("C:\\Users\\Filip\\Desktop\\PracaDyplomowa\\Testing\\tests\\"))
      "Copy inherit image" in {
         resourceManager.copyResources
         new File("C:\\Users\\Filip\\Desktop\\PracaDyplomowa\\Testing\\tests\\resources\\inherit.gif").exists mustEqual true
      }
   }

}