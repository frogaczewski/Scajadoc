package org.scajadoc.extractor

import tools.nsc.doc.model._

/**
 * Extractor of class/trait or methods parameters.
 *
 * @author Filip Rogaczewski
 */
class ParameterExtractor extends Extractor[HigherKinded, ParametersExtract] {

   def extract(info : HigherKinded) : Option[ParametersExtract] = {
      info match {
         case d : Trait => extractTemplateParameters(d.asInstanceOf[HigherKinded])
         case v : Def => extractDefinitionParameters(v)
      }
   }

   /**
    * Extracts the parameters of the generic type.
    */
   def extractTemplateParameters(template : HigherKinded) : Option[ParametersExtract] = {
      for (param <- template.typeParams) {
         val params = param.typeParams
         val raw = param.rawName
         val name = param.name
         new ParameterSignature {
            override def isGeneric = true
            override def isVararg = false
            override def isValue = false
            override def text = { ""
            }
         }
      }
      Some(new ParametersExtract {
         def parameters = Nil
         def text = ""
      })
   }

   /**
    * Extracts the parameters of the method definition.
    */
   def extractDefinitionParameters(defInfo : Def) = {
      for (valueParam <- defInfo.valueParams) {
         valueParam.foreach(v => {
            println("name " + v.name)
            println("result type " + v.resultType.name)
            println("is type param " + v.isTypeParam)
            println("is value param " + v.isValueParam)
            println("is function param " + v.isFunction)
            if (v.isFunction) {
               val funType = v.asInstanceOf[FunctionParam].funType
               println("fun name " + funType.name)
            }
            if (v.isTuple) {
               val tup = v.asInstanceOf[TupleParam].tupleType
               println("tupe name " + tup.name)
            }
         })
      }
      None
   }

}

trait ParametersExtract extends Extract {
   def parameters : List[(ParameterEntity, ParameterSignature)]
}

trait ParameterSignature {
   def text : String
   def isGeneric : Boolean
   def isVararg : Boolean
   def isValue : Boolean
}