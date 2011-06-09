package org.scajadoc.extractor

import tools.nsc.doc.model._

/**
 * Class for extracting java information from both: defs and vals.
 * Vals are extracted only when they're functions.
 *
 * @author Filip Rogaczewski
 */
class MethodExtractor extends Extractor[NonTemplateMemberEntity, MethodExtract] {

   object anyRefMethods {

      /**
      * Scala methods which comes from AnyRef.
      */
      val value = List(
         "$asInstanceOf",
         "$isInstanceOf",
         "$eq$eq",
         "eq",
         "synchronized",
         "$bang$eq",
         "ne",
         "$hash$hash"
      )

      def contains(methodName : String) : Boolean = value.contains(methodName)
   }

   def extract(info: NonTemplateMemberEntity) : Option[MethodExtract] = {
      if (info.isConstructor && info.inDefinitionTemplates.contains(info.inTemplate)) {
         Some(new ConstructorExtractImpl(info))
      } else if (info.inDefinitionTemplates.contains(info.inTemplate))
         Some(new MethodExtractImpl(info))
      else if (!anyRefMethods.contains(info.rawName))
         Some(new InheritedMethodExtractImpl(info))
      else if (info.isInstanceOf[Val] && info.asInstanceOf[Val].settersAndGetters)
         Some(new GetterMethodExtractImpl(info))
      else if (info.isInstanceOf[Val] && !info.asInstanceOf[Val].settersAndGetters)
         Some(new MethodExtractImpl(info))
      else
         None
   }

   class MethodExtractImpl(val info : NonTemplateMemberEntity) extends MethodExtract {
      def name = info.rawName

      def allocation = {
         Allocation.extract(info)
      }

      /**
       * TODO change to typeextractor
       */
      def typ = {
         val typ = info.resultType
         val rsn = info.resultType.name
         if (info.resultType.name.equalsIgnoreCase("unit"))
            "void"
         else
            info.resultType.name
      }

      def entity = info

      def isInherited = false

      def inTemplate = info.inTemplate

      def isExecutable = true

      def parameters = {
         def makeParam(v : ValueParam) = {
            if (v.isFunction)
               v.asInstanceOf[FunctionParam].funType.name
            else {
               val x = v.resultType + " " + v.name
               x
            }
         }
         def makeParams(params : List[List[ValueParam]]) =
            "(" + params.flatten.map(makeParam(_)).mkString(", ") + ")"
         if (info.isInstanceOf[Def])
            makeParams(info.asInstanceOf[Def].valueParams)
         else if (info.isInstanceOf[Constructor])
            makeParams(info.asInstanceOf[Constructor].valueParams)
         else
            "()"
      }
   }

   class ConstructorExtractImpl(info : NonTemplateMemberEntity) extends MethodExtractImpl(info) with ConstructorExtract {
      override def name = info.inTemplate.rawName
      override def typ =  info.inTemplate.rawName
   }

   class InheritedMethodExtractImpl(info : NonTemplateMemberEntity) extends MethodExtractImpl(info) with InheritedMember {
      override def isInherited = true
      def inDefinitionTemplates = info.inDefinitionTemplates
   }

   class GetterMethodExtractImpl(info : NonTemplateMemberEntity) extends MethodExtractImpl(info) {
      override def name = "get" + info.rawName.capitalize
   }

}

/**
 * Extract of Java information about the method from Scala source model.
 *
 * @author Filip Rogaczewski
 */
trait MethodExtract extends MemberExtract {
   def parameters : String
}

trait ConstructorExtract extends MethodExtract