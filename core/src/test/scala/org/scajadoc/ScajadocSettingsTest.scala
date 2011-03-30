package org.scajadoc

import org.specs.runner.{JUnitSuiteRunner, JUnit4}
import org.junit.runner.RunWith
import org.specs.Specification
import org.specs.mock.Mockito

/**
 * Unit tests for reading Scajadoc settings.
 *
 * @author Filip Rogaczewski
 */
@RunWith(classOf[JUnitSuiteRunner])
class ScajadocSettingsRunAsTest  extends JUnit4(scajadocSettingsTest)

object scajadocSettingsTest extends Specification("Specification of link resolver") with Mockito {

   "Scajadoc link setting" should {
      "Read single well formed link following -link option" in {
         val args = Array("-link", "http://download.oracle.com/javase/6/docs/api/")
         DocSettings(args).links mustContain "http://download.oracle.com/javase/6/docs/api/"
      }
      "Read multiple well formed links following -link option" in {
         val args = Array("-link", "http://download.oracle.com/javase/6/docs/api/", "http://download.oracle.com/javaee/6/api/")
         DocSettings(args).links mustContain "http://download.oracle.com/javase/6/docs/api/"
         DocSettings(args).links mustContain "http://download.oracle.com/javaee/6/api/"
      }
      "Read single not-well formed link following -link option" in {
         val args = Array("-link", "http://download.oracle.com/javase/6/docs/api")
         DocSettings(args).links mustContain "http://download.oracle.com/javase/6/docs/api/"
      }
      "Validate and report broken links following -link option" in {
         val args = Array("-link", "http://download.oracle.com/javase/6/docs/a")
         DocSettings(args) must throwA[IllegalArgumentException]
      }
   }

}