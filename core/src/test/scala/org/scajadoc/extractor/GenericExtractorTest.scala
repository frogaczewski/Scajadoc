package org.scajadoc.extractor

import org.junit.runner.RunWith
import org.specs.runner.{JUnit4, JUnitSuiteRunner}
import org.specs.Specification
import org.specs.mock.Mockito
import org.scajadoc.util.{TemplateGenerator}
import org.scajadoc.util.extractor.{Signature, Extractor}
import tools.nsc.doc.model.MemberEntity

/**
 * Unit test of generic extractor.
 *
 * @author Filip Rogaczewski
 */
@RunWith(classOf[JUnitSuiteRunner])
class GenericExtractorRunAsTest  extends JUnit4(genericExtractorTest)

object genericExtractorTest extends Specification("Specification of generic extractor") with Mockito {

   var generator : TemplateGenerator = _

   private val path = "src/test/resources"

   private val extractor = new Extractor[MemberEntity, Signature] {
      def extract(info : MemberEntity) : Signature = null
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