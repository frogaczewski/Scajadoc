package org.scajadoc.distributed

/**
 * @author Filip Rogaczewski
 */
object Executor {

	@throws(classOf[NoAlgorithmException])
	def execute(algorithm : Option[Algorithm[Object]]) = {
		algorithm match {
			case Some(a) => a.execute(null)
			case None => throw new NoAlgorithmException("Algorithm is not supported.")
		}
	}

}

class NoAlgorithmException(message : String) extends Exception(message)