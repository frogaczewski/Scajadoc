package org.scajadoc.distributed

/**
 * @author Filip Rogaczewski
 */
case class DistributedAlgorithm(name : String, algorithm : Option[Algorithm[Object]]) {
   final val algName = "ds"
}

case object HSDistributedAlgorithm extends DistributedAlgorithm("HS", None)