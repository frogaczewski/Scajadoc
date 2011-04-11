package org.scajadoc.extractor

import tools.nsc.doc.model.{Entity, MemberEntity, DocTemplateEntity}

/**
 * Container holding basic entity queries. 
 *
 * @author Filip Rogaczewski
 */
object entityQueryContainer {

	/**
	 * Returns true if the entity is a root package.
	 */
	val isRootPackage : (Entity => Boolean) =
		(entity : Entity) => (entity.isInstanceOf[DocTemplateEntity]
				&& entity.asInstanceOf[DocTemplateEntity].isRootPackage)

	/**
	 * Returns true if the entity is a package.
	 */
	val isPackage : (Entity => Boolean) =
		(entity : Entity) => (entity.isInstanceOf[DocTemplateEntity]
				&& entity.asInstanceOf[DocTemplateEntity].isPackage
				&& !isRootPackage(entity))

	/**
	 * Returns true if the entity a class.
	 */
	val isType : (MemberEntity => Boolean) =
		(entity : MemberEntity) => (entity.isInstanceOf[DocTemplateEntity]
				&& !entity.asInstanceOf[DocTemplateEntity].isPackage)

	val isInterface : (DocTemplateEntity => Boolean) =
		(entity : DocTemplateEntity) => entity.isTrait

	/**
	 * Returns true if the entity is subclass of given class.
	 */
	val isSubclassOf : ((DocTemplateEntity, Class[_]) => Boolean) =
		(entity : DocTemplateEntity, clazz : Class[_]) =>
				entity.linearizationTemplates.exists(_.qualifiedName == clazz.getCanonicalName)

	/**
	 * Returns true if the entity is an exception.
	 */
	val isException : (DocTemplateEntity => Boolean) =
		(t : DocTemplateEntity) => isSubclassOf(t, classOf[java.lang.Exception])

	/**
	 * Returns true if the entity is an error.
	 */
	val isError : (DocTemplateEntity => Boolean) =
		(t : DocTemplateEntity) => isSubclassOf(t, classOf[java.lang.Error])

	/**
	 * Returns true if the entity is an annotation.
	 */
	val isAnnotation : (DocTemplateEntity => Boolean) =
		(t : DocTemplateEntity) => isSubclassOf(t, classOf[scala.Annotation])

   val isMemberAnnotation : (MemberEntity => Boolean) =
      (t : MemberEntity) => t.isInstanceOf[DocTemplateEntity] && isAnnotation(t.asInstanceOf[DocTemplateEntity])

	val isEnumeration : (DocTemplateEntity => Boolean) =
		(t : DocTemplateEntity) => isSubclassOf(t, classOf[scala.Enumeration])


}