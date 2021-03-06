package org.scajadoc.extractor

import tools.nsc.doc.model.{Val, Entity, MemberEntity, DocTemplateEntity, TemplateEntity, ConstantVal}

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
	 * Returns true if the entity is a type.
	 */
	val isType : (Entity => Boolean) =
		(entity : Entity) => (entity.isInstanceOf[TemplateEntity]
				&& !entity.asInstanceOf[TemplateEntity].isPackage
            && !(entity.isInstanceOf[DocTemplateEntity] && isAnnotation(entity.asInstanceOf[DocTemplateEntity])))

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

   /**
    * Returns true if the entity is an enum.
    */
	val isEnumeration : (DocTemplateEntity => Boolean) =
		(t : DocTemplateEntity) => isSubclassOf(t, classOf[scala.Enumeration])

   /**
    * Returns true if the entity is a field.
    */
   val isField : (MemberEntity => Boolean) =
      (m : MemberEntity) => (m.isVal || m.isVar) && !isFunction(m)

   /**
    * Returns true if the entity is a function.
    */
   val isFunction : (MemberEntity => Boolean) = m => m.isFunction

   /**
    * TODO reimplement
    */
   val isGetter : (MemberEntity => Boolean) =
      m => m.isInstanceOf[Val]

   val isMethod : (MemberEntity => Boolean) =
      (m : MemberEntity) => m.isDef || isFunction(m) || isGetter(m) && !isConstructor(m)

   val isConstructor : (MemberEntity => Boolean) = m => m.isConstructor


   def isConstant : MemberEntity => Boolean =
      (m : MemberEntity) => m.isInstanceOf[ConstantVal]

   /**
    * Returns true if the package should be documented by Scajadoc.
    */
   def isDocumentablePackage : DocTemplateEntity => Boolean =
      (pack : DocTemplateEntity) => pack.members.filter(!isPackage(_)).size != 0

}