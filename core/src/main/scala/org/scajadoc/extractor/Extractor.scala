package org.scajadoc.extractor

import tools.nsc.doc.model._

/**
 * Interface for all extractors, which extract informations
 * required by Javadoc from the Scala model.
 *
 * @author Filip Rogaczewski
 */
trait Extractor[T <: Entity, X <: Extract] {

   /**
    * Extracts Javadoc information from the Scala model.
    */
   def extract(info : T) : Option[X]

   /**
    * TODO add 'default' handling.
    * TODO use AccessModifier instead.
    */
   @deprecated
   def extractVisibility(visibility : Visibility) : String = {
      if (!visibility.isProtected && !visibility.isPublic)
         "private"
      else if (visibility.isProtected)
         "protected"
      else
         "public"
   }

}

/**
 * Encapsulation of Java information extracted from Scala model.
 *
 * @author Filip Rogaczewski
 */
trait Extract {

   /**
    * Raw name of the Java construct.
    */
   def name : String


   /**
    * Returns extracted entity.
    */
   def entity : MemberEntity

   /**
    * Returns name of the type, class, method, field, etc.
    */
   def typ : String

}

/**
 * Encapsulation of Java information extracted from a member entity.
 *
 * @author Filip Rogaczewski
 */
trait MemberExtract extends Extract {

   /**
    * Returns the object allocation type. Static or dynamic.
    */
   def allocation : Allocation.Allocation

   /**
    * Returns type of the entity.
    */
   def typ : String

   /**
    * Returns true if the member is inherited from a class or an interface.
    */
   def isInherited : Boolean

   def inTemplate : DocTemplateEntity
}

/**
 * Trait for fields or methods which are inherited.
 *
 * @author Filip Rogaczewski
 */
trait InheritedMember extends MemberExtract {
   def inDefinitionTemplates  : List[TemplateEntity]
}

/**
 * JVM allocation modifiers.
 *
 * @author Filip Rogaczewski
 */
object Allocation extends Enumeration {
   type Allocation = Value
   val Static = Value("static")
   val Dynamic = Value("")

   def extract(info : MemberEntity) = {
      if (info.inTemplate.isObject)
         Static
      else
         Dynamic
   }
}

/**
 * Access modifiers recognizable by Java.
 *
 * @author Filip Rogaczewski
 */
object AccessModifier extends Enumeration {
   type AccessModifier = Value
   val Public = Value("public")
   val Protected = Value("protected")
   val Default = Value("")
   val Private = Value("private")

   /**
    * Extracts visibility into form of java access modifier.
    *
    * TODO add default handling
    */
   def extract(visibility : Visibility) = {
      if (!visibility.isProtected && !visibility.isPublic)
         Private
      else if (visibility.isProtected)
         Protected
      else
         Public
   }
}