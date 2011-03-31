package org.scajadoc

import page.SingleIndexPage
import org.specs.Specification
import tools.nsc.doc.model.DocTemplateEntity
import org.junit.runner.RunWith
import org.specs.runner.{JUnit4, JUnitSuiteRunner}
import org.specs.mock.{Mockito}

@RunWith(classOf[JUnitSuiteRunner])
class EntityQueryMockRunAsTest extends JUnit4(entityQueryMock)

/**
 * Unit tests for entity queries.
 *
 * @author Filip Rogaczewski
 */
object entityQueryMock extends Specification("Specification of the entity queries") with Mockito {

	var mockedTemplate : DocTemplateEntity = _

	"Collect all indexable query" should {
		doBefore {
			mockedTemplate = mock[DocTemplateEntity]
		}
		"collect templates" in {
			mockedTemplate.isTemplate returns true
			new SingleIndexPage(null).collectCondition(mockedTemplate) mustBe true

		}
		"does not collect entities which are not defs, vars, vals constructors or templates" in {
			mockedTemplate.isTemplate returns false
			new SingleIndexPage(null).collectCondition(mockedTemplate) mustBe false
		}
		"collect templates which are not defs" in {
			mockedTemplate.isTemplate returns true
			mockedTemplate.isDef returns false
			new SingleIndexPage(null).collectCondition(mockedTemplate) mustBe true
		}
		"collect definitions" in {
			mockedTemplate.isDef returns true
			new SingleIndexPage(null).collectCondition(mockedTemplate) mustBe true
		}
		"collect definitions which are not vars, templates, constructors" in {
			mockedTemplate.isDef returns true
			mockedTemplate.isVar returns false
			mockedTemplate.isVal returns false
			mockedTemplate.isTemplate returns false
			mockedTemplate.isConstructor returns false
			new SingleIndexPage(null).collectCondition(mockedTemplate) mustBe true
		}
		"collect vars" in {
			mockedTemplate.isVar returns true
			mockedTemplate.isDef returns false
			mockedTemplate.isVal returns false
			mockedTemplate.isTemplate returns false
			mockedTemplate.isConstructor returns false
			new SingleIndexPage(null).collectCondition(mockedTemplate) mustBe true
		}
		"collect vals" in {
			mockedTemplate.isVar returns false
			mockedTemplate.isDef returns false
			mockedTemplate.isVal returns true
			mockedTemplate.isTemplate returns false
			mockedTemplate.isConstructor returns false
			new SingleIndexPage(null).collectCondition(mockedTemplate) mustBe true
		}
		"collect constructors" in {
			mockedTemplate.isVar returns false
			mockedTemplate.isDef returns false
			mockedTemplate.isVal returns false
			mockedTemplate.isTemplate returns false
			mockedTemplate.isConstructor returns true
			new SingleIndexPage(null).collectCondition(mockedTemplate) mustBe true
		}
	}

}