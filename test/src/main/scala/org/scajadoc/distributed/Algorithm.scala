package org.scajadoc.distributed

/**
 * @author Filip Rogaczewski
 */
trait Algorithm[A] {
	def execute(a : A)
}