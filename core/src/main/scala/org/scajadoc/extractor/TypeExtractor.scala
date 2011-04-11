package org.scajadoc.extractor

import tools.nsc.doc.model.{TemplateEntity, DocTemplateEntity}

/**
 * Class for extracting data of interfaces, classes, annotations, enums
 * and exceptions.
 *
 * @author Filip Rogaczewski
 */
class TypeExtractor extends Extractor[DocTemplateEntity, TypeExtract]{

   def extract(info : DocTemplateEntity) : Option[TypeExtract] = {
      if (entityQueryContainer.isAnnotation(info))
         None
      else
         Some(new TypeExtractImpl(info))
   }

   class TypeExtractImpl(info : DocTemplateEntity) extends TypeExtract {
      def name = info.name
      def text = {
         val builder = new StringBuilder
         builder ++= extractVisibility(info.visibility)
         builder ++= " "
         if (info.isAbstract && !info.isTrait)
            builder ++= "abstract "
         builder ++= typ
         builder ++= " "
         builder ++= name
         if (!info.isTrait && directSuperclass != Nil) {
            builder ++= "\nextends "
            builder ++= directSuperclass.name
         }
         if (interfaces != Nil) {
            if (info.isTrait)
               builder ++= "\nextends "
            else
               builder ++= "\nimplements "
               val names = for (i <- interfaces) yield i.name
               builder ++= names.mkString(", ")
         }
         builder.toString
      }

      def typ = {
         if (info.isPackage)
            "package"
         else if (info.isTrait)
            "interface"
         else if (entityQueryContainer.isEnumeration(info))
            "enum"
         else
            "class"
      }
      def superClasses = info.linearizationTemplates.filter(!_.isTrait).reverse.tail
      def interfaces = {
         val interfaces = new collection.mutable.HashSet[TemplateEntity]
         def addNonDuplicate(tmp : TemplateEntity) = {
            interfaces.find(_.qualifiedName == tmp.qualifiedName) match {
               case Some(_) => {}
               case None => interfaces += tmp
            }
         }
         interfaces ++= info.interfaces
         for (clazz <- superClasses) {
            if (clazz.isInstanceOf[DocTemplateEntity]) {
               clazz.asInstanceOf[DocTemplateEntity].interfaces.foreach(addNonDuplicate(_))
            }
         }
         val interfacesStack = new collection.mutable.Stack[TemplateEntity]
         interfacesStack.pushAll(info.interfaces)
         while (interfacesStack.nonEmpty) {
            val i = interfacesStack.pop
            if (i.isInstanceOf[DocTemplateEntity]) {
               i.asInstanceOf[DocTemplateEntity].interfaces.foreach(addNonDuplicate(_))
               interfacesStack.pushAll(i.asInstanceOf[DocTemplateEntity].interfaces)
            }
         }
         interfaces.toList
      }
      def enclosingClass = info.enclosingClass
      def inPackage = {
         var parentIterator = info.inTemplate
         while (!parentIterator.isPackage)
            parentIterator = parentIterator.inTemplate
         parentIterator
      }
      def subclasses = info.subClasses.filter(!_.isTrait)
      def subinterfaces = info.subClasses.filter(_.isTrait)
      def directSuperclass = superClasses.head
      def isClass = !isInterface && !isEnum
      def isInterface = entityQueryContainer.isInterface(info)
      def isEnum = entityQueryContainer.isEnumeration(info)
   }

}


trait TypeExtract extends Extract {
   def name : String
   def isEnum : Boolean
   def isInterface : Boolean
   def isClass : Boolean
   def typ : String

   /**
    * Returns list of super classes (without Any, which is not a Java keyword).
    * The returned list is in reverse order: first is Object.
    */
   def superClasses : List[TemplateEntity]

   /**
    * Returns list of interfaces of this template, list of interfaces implemented by its
    * superclasses and list of interfaces extended by its interfaces.
    */
   def interfaces : List[TemplateEntity]
   def enclosingClass : Option[TemplateEntity]
   def inPackage : TemplateEntity
   def subclasses : List[TemplateEntity]
   def subinterfaces : List[TemplateEntity]
   def directSuperclass : TemplateEntity
}