package org.scajadoc.extractor

import tools.nsc.doc.model._

/**
 * Class for extracting data of interfaces, classes, annotations, enums
 * and exceptions.
 *
 * @author Filip Rogaczewski
 */
class TypeExtractor extends Extractor[DocTemplateEntity, TypeExtract] {

   import entityQueryContainer._

   private val methodExtractor = new MethodExtractor

   private val fieldExtractor = new FieldExtractor

   def extract(info : DocTemplateEntity) : Option[TypeExtract] = {
      if (entityQueryContainer.isAnnotation(info))
         None
      else
         Some(new TypeExtractImpl(info))
   }

   class TypeExtractImpl(info : DocTemplateEntity) extends TypeExtract {

      def name = info.rawName

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
      def directSuperclass = superClasses.last
      def isClass = !isInterface && !isEnum
      def isInterface = entityQueryContainer.isInterface(info)
      def isEnum = isEnumeration(info)
      def visibility = extractVisibility(info.visibility)
      def isAbstract = info.isAbstract && !info.isTrait

      private val entityMethods = {
         def entMethods(ent : DocTemplateEntity) = {
            ent.members.filter(isMethod).map(e => {
               e match {
                  case d : Def => methodExtractor.extract(d)
                  case v : Val => methodExtractor.extract(v)
                  case _ => None
               }
            }).filter(_.isDefined).map(_.get)
         }
         info.companion match {
            case Some(companion) => entMethods(info) ++ entMethods(companion)
            case None => entMethods(info)
         }
      }

      private val entityFields = {
         def entFields(ent : DocTemplateEntity) = {
            ent.members.filter(isField).map(e => {
               e match {
                  case v : Val => fieldExtractor.extract(v)
                  case _ => None
               }
            }).filter(_.isDefined).map(_.get)
         }
         info.companion match {
            case Some(companion) => entFields(info) ++ entFields(companion)
            case None => entFields(info)
         }
      }

      def constructors = {
         info.members.filter(isConstructor).map(e => {
            e match {
               case c : Constructor => methodExtractor.extract(c)
               case _ => None
            }
         }).filter(_.isDefined).map(_.get)
      }

      def inheritedFields = entityFields.filter(_.isInherited).map(_.asInstanceOf[InheritedMember])
      def fields = entityFields.filter(!_.isInherited)
      def inheritedMethods = entityMethods.filter(_.isInherited).map(_.asInstanceOf[InheritedMember])
      def methods = entityMethods.filter(!_.isInherited)
   }

}


trait TypeExtract extends Extract {
   def name : String
   def isEnum : Boolean
   def isInterface : Boolean
   def isClass : Boolean
   def typ : String

   def isAbstract : Boolean

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

   /**
    * Returns list of documentable methods of the entity and its companion.
    */
   def methods : List[MethodExtract]

   /**
    * Returns list of documentable inherited methods of the entity and its companion.
    */
   def inheritedMethods : List[InheritedMember]

   /**
    * Returns list of documentable fields of the entity and its companion.
    */
   def fields : List[FieldExtract]

   /**
    * Returns list of documentable inherited fields of the entity and its companion.
    */
   def inheritedFields : List[InheritedMember]

   /**
    * Returns list of documentable constructors.
    */
   def constructors : List[MethodExtract]

   /**
    * Returns list of subclasses which extend this template.
    */
   def subclasses : List[TemplateEntity]

   /**
    * Returns list of subinterfaces extending this template.
    */
   def subinterfaces : List[TemplateEntity]

   /**
    * Return direct superclass of this template.
    */
   def directSuperclass : TemplateEntity
   def visibility : String
}