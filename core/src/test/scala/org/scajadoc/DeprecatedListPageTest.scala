package org.scajadoc

import page.DeprecatedListPage
import org.junit.runner.RunWith
import org.specs.runner.{JUnit4, JUnitSuiteRunner}
import org.specs.Specification
import org.specs.mock.{Mockito}
import tools.nsc.doc.model.{DocTemplateEntity, MemberEntity}

/**
 * @author Filip Rogaczewski
 */
@RunWith(classOf[JUnitSuiteRunner])
class DeprecatedListPageRunAsTest extends JUnit4(deprecatedListPageTest)

object deprecatedListPageTest extends Specification("Specification of deprecated list page") with Mockito {

	var mockedTemplate : MemberEntity = _

	"Deprecated list page" should {
		doBefore {
			mockedTemplate = mock[MemberEntity]
         mockedTemplate.inTemplate returns {
            val inTemp = mock[DocTemplateEntity]
            inTemp.linearizationTemplates returns Nil
            inTemp
         }
		}
		"collect deprecated methods" in {
			mockedTemplate.deprecation returns Some(null)
			mockedTemplate.isDef returns true
			new DeprecatedListPage(null).collectDeprecatedMethods(mockedTemplate) mustBe true
		}
		"not-collect no-deprecated methods" in {
			mockedTemplate.deprecation returns None
			mockedTemplate.isDef returns true
			new DeprecatedListPage(null).collectDeprecatedMethods(mockedTemplate) mustBe false
		}
		"collect deprecated vals" in {
			mockedTemplate.deprecation returns Some(null)
			mockedTemplate.isVal returns true
			mockedTemplate.isVar returns false
			new DeprecatedListPage(null).collectDeprecatedFields(mockedTemplate) mustBe true
		}
		"not-collect no-deprecated vals" in {
			mockedTemplate.deprecation returns None
			mockedTemplate.isVal returns false
			mockedTemplate.isVar returns false
			new DeprecatedListPage(null).collectDeprecatedFields(mockedTemplate) mustBe false
		}
		"collect deprecated vars" in {
			mockedTemplate.deprecation returns Some(null)
			mockedTemplate.isVal returns false
			mockedTemplate.isVar returns true
			new DeprecatedListPage(null).collectDeprecatedFields(mockedTemplate) mustBe true
		}
		"not-collect no-deprecated vars" in {
			mockedTemplate.deprecation returns None
			mockedTemplate.isVal returns false
			mockedTemplate.isVar returns false
			new DeprecatedListPage(null).collectDeprecatedFields(mockedTemplate) mustBe false
		}
		"collect deprecated constructors" in {
			mockedTemplate.deprecation returns Some(null)
			mockedTemplate.isConstructor returns true
			new DeprecatedListPage(null).collectDeprecatedConstructors(mockedTemplate) mustBe true
		}
		"not-collect no-deprecated constructors" in {
			mockedTemplate.deprecation returns None
			mockedTemplate.isConstructor returns true
			new DeprecatedListPage(null).collectDeprecatedConstructors(mockedTemplate) mustBe false
		}
		"not-collect deprecated non-constructor" in {
			mockedTemplate.deprecation returns Some(null)
			mockedTemplate.isConstructor returns false
			new DeprecatedListPage(null).collectDeprecatedConstructors(mockedTemplate) mustBe false
		}
		"collect deprecated exceptions" in {
			mockedTemplate.deprecation returns Some(null)
//			mockedTemplate.isException returns true
			new DeprecatedListPage(null).collectDeprecatedExceptions(mockedTemplate) mustBe false
		}
	}

}