/**
 * Test classes with Scala generics. 
 */
class SimpleGeneric[A] {

   def mixedGenericMethod[B](b : B, name : String) : String = null

   def methodWithSimpleFunction(fun : Boolean => String => Boolean) : Boolean = true

   def methodWithTuple(tu : (String, Boolean)) = true

}

class CovariantGeneric[+C]

class ContrvariantGeneric[-D]

//class ExtendsGeneric[D >: SimpleGeneric[_] <: ExtendsGeneric[_]]

