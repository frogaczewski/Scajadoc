package org.scajadoc.page

import org.junit.runner.RunWith
import org.specs.runner.{JUnit4, JUnitSuiteRunner}
import org.specs.Specification
import org.specs.mock.Mockito
import org.scajadoc.util.TemplateGenerator
import org.scajadoc.extractor.TypeExtractor
import tools.nsc.doc.model.DocTemplateEntity
import xml.NodeSeq
import org.scajadoc.settings

/**
 * 
 * @author Filip Rogaczewski
 */
@RunWith(classOf[JUnitSuiteRunner])
class TypePageRunAsTest  extends JUnit4(typePageTest)

object typePageTest extends Specification("Specification of type page") with Mockito {

   var generator : TemplateGenerator = _

   private val path = "src/test/resources"

   private val extractor = new TypeExtractor

   "TypePage should contain information about implemented interfaces" should {
      doBefore {
         generator = new TemplateGenerator
         settings.sourcepath.value = path
      }
      "Recognize no-implemented interfaces" in {
         val index = generator.generate(path, "Index")(0)
         new TypePage(index.asInstanceOf[DocTemplateEntity]).implementedInterfaces mustEqual
         (<dl><dt><b>All Implemented Interfaces:</b><dd></dd></dt></dl>)
      }
      "Recognize implemented interfaces without links" in {
         val index = generator.generate(path, "Impl")(0)
         new TypePage(index.asInstanceOf[DocTemplateEntity]).implementedInterfaces mustEqual
         (<dl><dt><b>All Implemented Interfaces:</b><dd><xml:group>SimpleInterface</xml:group></dd></dt></dl>)
      }
   }
   
}