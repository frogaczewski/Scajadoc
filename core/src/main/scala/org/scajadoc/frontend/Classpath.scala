package org.scajadoc.frontend

import tools.nsc.doc.model._
import collection.mutable.HashMap
import java.io.File._

/**
 * Cache containing classpath entries associated with entities representing
 * Scala language constructs.
 *
 * @author Filip Rogaczewski
 */
object classpathCache {

	private val cache = new HashMap[Entity, Classpath]

	def apply(filename : String) : Classpath = {
		new Classpath(null,null) {
			override def canonicalClasspath = ""
			override def packageCanonicalPath = ""
			override def docPackageClasspath = "api" + separator
			override def docFileClasspath = docPackageClasspath + filename
		}
	}

	def apply(entity : Entity) : Classpath = {
		cache.get(entity) match {
			case Some(path) => path
			case None => {
				val path = new Classpath(entity.toRoot.reverse, entity)
				cache += entity -> path
				path
			}
		}
	}
}

/**
 * Encapsulation of classpath. 
 */
class Classpath (private val path : List[Entity], private val clpEntity : Entity) {

	import entityQueryContainer._
	
	/**
	 * Returns canonical classpath of the member entity.
	 */
	def canonicalClasspath() : String = path.map(entity => entity.name).mkString(".")

   /**
    *
    */
   def canonicalFileClasspath() : String = path.map(_.name).mkString("/")


   /**
    *  Returns canonical classpath of the member's package.
    */
	def packageCanonicalPath() : String = path.filter(isPackage).map(_.name).mkString(".")

	/**
	 * Returns path to the documentation of this template's package.
	 */
	def docPackageClasspath() : String = "api" + separator +
			path.filter(isPackage).map(_.name).mkString(separator)

	/**
	 * Returns path to the documentation of this template.
	 */
	def docFileClasspath() : String = "api" + separator + path.filter(entity =>
		entity.isInstanceOf[DocTemplateEntity]
		&& !entity.asInstanceOf[DocTemplateEntity].isRootPackage).map(_.name).mkString(separator)

	/**
	 * Returns relative path (from api base directory) to (class, interface, enum, etc..) entity's
	 * documentation.
	 */
	def docBaseFileClasspath() : String = path.filter(entity =>
		entity.isInstanceOf[DocTemplateEntity] && !isRootPackage(entity)).map(_.name).mkString(separator)

	/**
	 * Returns a relative path (from api base directory) to the entity's documentation.
    *
    * TODO move this functionality to LinkResolver.
	 */
   @deprecated
	def docBaseClasspath() : String = {
		val base = docBaseFileClasspath + ".html#"
		if (clpEntity.isInstanceOf[NonTemplateMemberEntity])
			base + clpEntity.name + entityPresentationUtil.params(clpEntity.asInstanceOf[NonTemplateMemberEntity])
		else
			base
	}

}