package org.scajadoc.mthesis

object FreeFallAcceleration {
   private val StandardGravity = 9.806d

   val freeFall : Double => Double =
      m => m * StandardGravity

   def main(args : Array[String]) = {
      println(freeFall(80))
   }
}
