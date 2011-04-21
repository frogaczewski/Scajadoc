package org.scajadoc.extractor

import org.junit.runner.RunWith
import org.specs.runner.{JUnit4, JUnitSuiteRunner}
import org.specs.Specification
import org.specs.mock.Mockito
import org.scajadoc.util.{TemplateGenerator}
import tools.nsc.doc.model.DocTemplateEntity
import org.scajadoc.extractor._

/**
 * Unit test for TypeExtractor class.
 *
 * @author Filip Rogaczewski
 */
@RunWith(classOf[JUnitSuiteRunner])
class TypeExtractorRunAsTest  extends JUnit4(typeExtractorTest)

object typeExtractorTest extends Specification("Specification of type extractor") with Mockito {

   var generator : TemplateGenerator = _

   private val path = "src/test/resources"

   private val extractor = new TypeExtractor

   "TypeExtractor typ" should {
      doBefore(generator = new TemplateGenerator)
      "Extract class type information from simple scala class" in {
         val index = generator.generate(path, "Index")(0)
         extractor.extract(index.asInstanceOf[DocTemplateEntity]).get.typ mustEq "class"
      }
      "Extract class type information from scala sealed class" in {
         val index = generator.generate(path, "SealedIndex")(0)
         extractor.extract(index.asInstanceOf[DocTemplateEntity]).get.typ mustEq "class"
      }
      "Extract class type information from case class" in {
         val index = generator.generate(path, "CaseIndex")(0)
         extractor.extract(index.asInstanceOf[DocTemplateEntity]).get.typ mustEq "class"
      }
      "Extract annotation type information from simple scala class" in {
         val annot = generator.generate(path, "Annot")(0)
         extractor.extract(annot.asInstanceOf[DocTemplateEntity]) mustBe None
      }
      "Extract class type information from scala object" in {
         val index = generator.generate(path, "Obj")(0)
         extractor.extract(index.asInstanceOf[DocTemplateEntity]).get.typ mustEq "class"
      }
      "Extract class type information from scala case object" in {
         val index = generator.generate(path, "CaseObj")(0)
         extractor.extract(index.asInstanceOf[DocTemplateEntity]).get.typ mustEq "class"
      }
      "Extract class type information from scala abstract class" in {
         val index = generator.generate(path, "AbstractIndex")(0)
         extractor.extract(index.asInstanceOf[DocTemplateEntity]).get.typ mustEq "class"
      }
      "Extract interface information from simple scala trait" in {
         val interface = generator.generate(path, "SimpleInterface")(0)
         extractor.extract(interface.asInstanceOf[DocTemplateEntity]).get.typ mustEq "interface"
      }
      "Extract enum information from simple scala enumeration" in {
         val enumeration = generator.generate(path, "SimpleEnumeration")(0)
         extractor.extract(enumeration.asInstanceOf[DocTemplateEntity]).get.typ mustEq "enum"
      }
      "Extract package information from scala package" in {

      }
   }

   "TypeExtractor signature text" should {
      doBefore(generator = new TemplateGenerator)
      "Extract public abstract class information from simple scala class" in {
         val index = generator.generate(path, "AbstractIndex")(0)
         (extractor.extract(index.asInstanceOf[DocTemplateEntity]).get.name
               mustEqual "public abstract class AbstractIndex\nextends Object\nimplements ScalaObject")
      }
      "Extract public class information from simple scala class" in {
         val index = generator.generate(path, "SealedIndex")(0)
         (extractor.extract(index.asInstanceOf[DocTemplateEntity]).get.name
            mustEqual "public class SealedIndex\nextends Object\nimplements ScalaObject")
      }
      "Return none in case of public scala annotation" in {
         val index = generator.generate(path, "AdvAnnot")(0)
         extractor.extract(index.asInstanceOf[DocTemplateEntity]) mustBe None
      }
      "Extract public interface information from simple scala trait" in {
         val index = generator.generate(path, "SimpleInterface")(0)
         (extractor.extract(index.asInstanceOf[DocTemplateEntity]).get.name
            mustEqual "public interface SimpleInterface")
      }
      "Extract public interface from scala trait with inheritance" in {
         val index = generator.generate(path, "AdvInterface")(0)
         (extractor.extract(index.asInstanceOf[DocTemplateEntity]).get.name
            mustEqual "public interface AdvInterface\nextends SimpleInterface, AnotherSimpleInterface")
      }
   }

}