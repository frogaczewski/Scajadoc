package org.scajadoc.distributed

/**
 * @author Filip Rogaczewski
 */
@Tests("tests of hs algorithm")
@serializable
@remote
class HSAlgorithm extends Algorithm[Runnable] {

	def execute(a : Runnable) = {
		
	}

}

class ExtHSAlgorithm(val name : String) extends HSAlgorithm {
   override def execute(a : Runnable) = {}
}

class Ext2HSAlgorithm extends HSAlgorithm {}