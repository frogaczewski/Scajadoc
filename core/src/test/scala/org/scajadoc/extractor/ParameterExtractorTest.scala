package org.scajadoc.extractor

import org.junit.runner.RunWith
import org.specs.runner.{JUnit4, JUnitSuiteRunner}
import org.specs.Specification
import org.specs.mock.Mockito
import org.scajadoc.util.TemplateGenerator
import org.scajadoc.extractor._
import tools.nsc.doc.model.{HigherKinded, MemberEntity}

/**
 * Unit tests of parameter extractor.
 *
 * @author Filip Rogaczewski
 */
@RunWith(classOf[JUnitSuiteRunner])
class ParameterExtractorRunAsTest  extends JUnit4(parameterExtractorTest)

object parameterExtractorTest extends Specification("Specification of generics extractor") with Mockito {

   var generator : TemplateGenerator = _

   private val path = "src/test/resources"

   private val extractor = new ParameterExtractor

   implicit def memberToHigherKinded(member : MemberEntity) : HigherKinded = {
      member.asInstanceOf[HigherKinded]
   }
   
   "Parameter extractor generic information" should {
      doBefore(generator = new TemplateGenerator)
      "Extract simple generic method with generic and non-generic arguments" in {
         val generic = generator.generate(path, "mixedGenericMethod")(0)
         extractor.extract(generic).get.text mustEq "<B>"
      }
      "Extract simple function from method parameters" in {
         val generic = generator.generate(path, "methodWithSimpleFunction")(0)
         extractor.extract(generic).get.text mustEq ""
      }
      "Extract simple tupple from method parameters" in {
         val generic = generator.generate(path, "methodWithTuple")(0)
         extractor.extract(generic).get.text mustEq ""
      }
      /*"Extract simple generic information from scala class signature" in {
         val generic = generator.generate(path, "SimpleGeneric")(0)
         extractor.extract(generic).get.text mustEq "<A>"
      }
      "Extract generic information from covariant type" in {
         val generic = generator.generate(path, "CovariantGeneric")(0)
         extractor.extract(generic).get.text mustEq "<C>"
      }
      "Extract generic information from contrvariant type" in {
         val generic = generator.generate(path, "ContrvariantGeneric")(0)
         extractor.extract(generic).get.text mustEq "<D>"
      } */
   }

}