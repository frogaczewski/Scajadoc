package org.scajadoc.distributed

/**
 * // TODO: Document this
 * @author Filip
 * @since 4.0
 */
trait GenericType {

   def dos = println("xxx")
}
class GenericTest[A <% GenericType](val a : A) {
   def exec = a.dos
}