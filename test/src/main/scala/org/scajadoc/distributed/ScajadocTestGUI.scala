package org.scajadoc.test

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