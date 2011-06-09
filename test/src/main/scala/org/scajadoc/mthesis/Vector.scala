package org.scajadoc.mthesis

class Vector(val x : Double, val y : Double) {
   def +(other : Vector) = new Vector(x + other.x, y + other.y)
   def *(scalar : Double) = new Vector(scalar * x, scalar * y)
}
