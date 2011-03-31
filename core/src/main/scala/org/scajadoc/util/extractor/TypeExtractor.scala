package org.scajadoc.util.extractor

import org.scajadoc.entityQueryContainer
import tools.nsc.doc.model.{TemplateEntity, DocTemplateEntity}

/**
 * Class for extracting data of interfaces, classes, annotations, enums
 * and exceptions.
 *
 * @author Filip Rogaczewski
 */
class TypeExtractor extends Extractor[DocTemplateEntity, TypeSignature]{

   def extract(info : DocTemplateEntity) : TypeSignature = {
      new TypeSignatureImpl(info)
   }

   class TypeSignatureImpl(info : DocTemplateEntity) extends TypeSignature {
      def name = info.name
      def text = {
         val builder = new StringBuilder
         builder ++= extractVisibility(info.visibility)
         builder ++= " "
         if (info.isAbstract)
            builder ++= "abstract "
         builder ++= typ
         builder ++= " "
         builder ++= name
         builder ++= "\nextends "
         builder ++= directSuperclass.name
         if (interfaces != Nil) {
            builder ++= "\nimplements "
            interfaces.foreach(i => {
               builder ++= i.name
            })
         }
         builder.toString
      }
      def typ = {
         if (info.isPackage)
            "package"
         else if (info.isTrait)
            "interface"
         else if (entityQueryContainer.isAnnotation(info))
            "annotation type"
         else if (entityQueryContainer.isEnumeration(info))
            "enum"
         else
            "class"
      }
      def superClasses = info.linearizationTemplates.filter(!_.isTrait)
      def interfaces = info.interfaces
      def enclosingClass = {
         info.enclosingClass
      }
      def inPackage = {
         var parentIterator = info.inTemplate
         while (!parentIterator.isPackage)
            parentIterator = parentIterator.inTemplate
         parentIterator
      }
      def subclasses = info.subClasses.filter(!_.isTrait)
      def subinterfaces = info.subClasses.filter(_.isTrait)
      def directSuperclass = superClasses.head
   }

}