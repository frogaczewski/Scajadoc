package org.scajadoc.extractor

import tools.nsc.doc.model.{TemplateEntity, DocTemplateEntity, NonTemplateMemberEntity}

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
         info.resultType.name
      }

      def entity = info

      def isInherited = false

      def inTemplate = info.inTemplate
   }

   class ConstructorExtractImpl(info : NonTemplateMemberEntity) extends MethodExtractImpl(info) {
      override def name = info.inTemplate.rawName
   }

   class InheritedMethodExtractImpl(info : NonTemplateMemberEntity) extends MethodExtractImpl(info) with InheritedMember {
      override def isInherited = true
      def inDefinitionTemplates = info.inDefinitionTemplates
   }

}

/**
 * Extract of Java information about the method from Scala source model.
 *
 * @author Filip Rogaczewski
 */
trait MethodExtract extends MemberExtract {

   def inTemplate : DocTemplateEntity

}