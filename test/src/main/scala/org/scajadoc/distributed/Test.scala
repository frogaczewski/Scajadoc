import com.sun.org.apache.xalan.internal.xsltc.runtime.Operators

case class Test(val name : String)

object Test extends Application {
   println("hello world!")
}

object Static {
   val helloWorld = "Hello world"

   val staticFun : (String => Boolean) = (_== "Hello world")
}


trait ITest {
   def method : String
   def implMethod = {
      println("Hello world!")
   }
}

class FunctionsAndTuples {

   val fun : (String => Boolean) =
      (x : String) =>
         if (x eq "hello world") true
         else false

   def tupleReturn : (String, Boolean) = {
      ("hello world", true)
   }

}

class Operators {
   def +(o : Operators) : Operators = this
   def %(o : Operators) : Operators = new Operators
}









