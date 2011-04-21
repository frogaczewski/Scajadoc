package org.scajadoc.extractor

import tools.nsc.doc.model.Val

/**
 * Class for extracting data from vals and vars.
 *
 * @author Filip Rogaczewski
 */
class FieldExtractor extends Extractor[Val, FieldExtract] {

   object typeExtractor extends TypeExtractor

   def extract(info : Val) : Option[FieldExtract] = {
      if (info.inDefinitionTemplates.contains(info.inTemplate))
         Some(new FieldExtractImpl(info))
      else
         Some(new InheritedFieldImpl(info))
   }

   class FieldExtractImpl(info : Val) extends FieldExtract {

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
   }

   class InheritedFieldImpl(info : Val) extends FieldExtractImpl(info) with InheritedMember {
      def inDefinitionTemplates = info.inDefinitionTemplates
      override def isInherited = true
   }

}

/**
 * Extract of Java information about the field from Scala source model.
 *
 * @author Filip Rogaczewski
 */
trait FieldExtract extends MemberExtract {


}