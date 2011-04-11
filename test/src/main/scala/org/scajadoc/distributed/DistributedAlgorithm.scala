package org.scajadoc.distributed

/**
 * @author Filip Rogaczewski
 */
case class DistributedAlgorithm(name : String, algorithm : Option[Algorithm[Object]])

case object HSDistributedAlgorithm extends DistributedAlgorithm("HS", None)