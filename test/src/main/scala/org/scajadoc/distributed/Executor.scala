package org.scajadoc.distributed

/**
 * @author Filip Rogaczewski
 */
object Executor {

	@throws(classOf[NoAlgorithmException])
	def execute(algorithm : Option[Algorithm]) = {
		algorithm match {
			case Some(a) => a.execute
			case None => throw new NoAlgorithmException("Algorithm is not supported.")
		}
	}

}

class NoAlgorithmException(message : String) extends Exception(message)