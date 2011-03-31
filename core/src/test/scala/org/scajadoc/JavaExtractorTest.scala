package org.scajadoc

import org.junit.runner.RunWith
import org.specs.runner.{JUnit4, JUnitSuiteRunner}
import org.specs.Specification
import org.specs.mock.Mockito
import util.{javaExtractor, TemplateGenerator}
import tools.nsc.doc.model.DocTemplateEntity

/**
 * Unit test for TypeExtractor class.
 *
 * @author Filip Rogaczewski
 */
@RunWith(classOf[JUnitSuiteRunner])
class JavaExtractorRunAsTest  extends JUnit4(javaExtractorTest)

object javaExtractorTest extends Specification("Specification of java extractor") with Mockito {

}