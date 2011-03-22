package org.scajadoc

import org.junit.runner.RunWith
import org.specs.runner.{JUnit4, JUnitSuiteRunner}
import org.specs.Specification
import org.specs.mock.Mockito
import tools.nsc.Global
import tools.nsc.doc.model.ModelFactory

/**
 * @author Filip Rogaczewski
 */
@RunWith(classOf[JUnitSuiteRunner])
class ModelFactoryTestRunAsTest extends JUnit4(modelFactoryTest)

object modelFactoryTest extends Specification("Specification of the model factory") with Mockito {

	/*object compiler extends Global(settings, reporter) with tools.nsc.interactive.RangePositions {
		override protected def computeInternalPhases() {
		}
		phasesSet += syntaxAnalyzer
		phasesSet += analyzer.namerFactory
		phasesSet += analyzer.packageObjects
		phasesSet += analyzer.typerFactory
		phasesSet += superAccessors
		phasesSet += pickler
		phasesSet += refchecks
	}
*/
//	var mockedSymbol : compiler.Symbol = _
//	val modelFactory = (new ModelFactory(compiler, settings) with tools.nsc.model.comment.CommentFactory with tools.nsc.model.TreeFactory)

	/* "model factory" should {
		"recognize if class is an exception" in {
			mockedSymbol.isClass returns true
			modelFactory.makeMember(mockedSymbol, new modelFactory.DocTemplateImpl())
		}
	}*/


}