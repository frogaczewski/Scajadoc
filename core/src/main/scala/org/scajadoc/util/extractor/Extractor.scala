package org.scajadoc.util.extractor

import tools.nsc.doc.model.{Visibility, TemplateEntity, Entity}

/**
 * 
 *
 * @author Filip Rogaczewski
 */
trait Extractor[T <: Entity, X <: Signature] {

   def extract(info : T) : X

   /**
    * TODO add 'default' handling.
    */
   def extractVisibility(visibility : Visibility) : String = {
      if (!visibility.isProtected && !visibility.isPublic)
         "private"
      else if (visibility.isProtected)
         "protected"
      else
         "public"
   }

}

trait Signature {
   def name : String
   def text : String
}

trait TypeSignature extends Signature {
   def typ : String
   def superClasses : List[TemplateEntity]
   def interfaces : List[TemplateEntity]
   def enclosingClass : Option[TemplateEntity]
   def inPackage : TemplateEntity
   def subclasses : List[TemplateEntity]
   def subinterfaces : List[TemplateEntity]
   def directSuperclass : TemplateEntity
}