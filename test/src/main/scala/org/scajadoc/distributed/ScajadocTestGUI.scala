package org.scajadoc.test

import java.lang.annotation.{RetentionPolicy, Retention}

/**
 * @author Filip Rogaczewski
 */
@deprecated("Use ScajadocTest instead.")
class ScajadocTestGUI {

	@deprecated
	class ScajadocTestException extends Exception

	@deprecated
	def abort = throw new RuntimeException
}

@deprecated
class algorithm(val name : String) extends ClassfileAnnotation

@deprecated
object SupportedAlgorithm extends Enumeration {
	type SupportedAlgorithm = Value

	@deprecated
	val LCR, HS, ML = Value
}

@deprecated
@algorithm(name = "test algorithm")
trait testAlgorithm