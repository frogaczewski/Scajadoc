package org.scajadoc.extractor

import tools.nsc.doc.model._

/**
 * 
 *
 * @author Filip Rogaczewski
 */
trait Extractor[T <: Entity, X <: Extract] {

   def extract(info : T) : Option[X]

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

trait Extract {
   def text : String
}