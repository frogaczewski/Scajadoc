package org.scajadoc.extractor

import org.junit.runner.RunWith
import org.specs.runner.{JUnit4, JUnitSuiteRunner}
import org.specs.Specification
import org.specs.mock.Mockito
import org.scajadoc.util.{TemplateGenerator}
import org.scajadoc.extractor._
import tools.nsc.doc.model.MemberEntity

/**
 * Unit test of extracting visibility. 
 *
 * @author Filip Rogaczewski
 */
@RunWith(classOf[JUnitSuiteRunner])
class VisibilityExtractorRunAsTest  extends JUnit4(visibilityExtractorTest)

object visibilityExtractorTest extends Specification("Specification of visibility extractor") with Mockito {

   var generator : TemplateGenerator = _

   private val path = "src/test/resources"

   private val extractor = new Extractor[MemberEntity, Extract] {
      def extract(info : MemberEntity) : Option[Extract] = None
   }
   
   "Extractor extract visibility " should {
      doBefore(generator = new TemplateGenerator)
      "Extract public from class with public visibility" in {
         val entity = generator.generate(path, "PublicClass")(0)
         extractor.extractVisibility(entity.visibility) mustEq "public"
      }
      "Extract private from class with private visibility" in {
         val entity = generator.generate(path, "PrivateClass")(0)
         extractor.extractVisibility(entity.visibility) mustEq "private"
      }
      "Extract private from class with protected visibility" in {
         val entity = generator.generate(path, "ProtectedClass")(0)
         extractor.extractVisibility(entity.visibility) mustEq "protected"
      }
   }

}