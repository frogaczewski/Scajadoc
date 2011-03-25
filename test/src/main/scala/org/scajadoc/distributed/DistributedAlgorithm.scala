package org.scajadoc.distributed

/**
 * @author Filip Rogaczewski
 */
case class DistributedAlgorithm(name : String, algorithm : Option[Algorithm])

case object HSDistributedAlgorithm extends DistributedAlgorithm("HS", None)