package org.scajadoc.page

import xml.Node
import tools.nsc.doc.model.{DocTemplateEntity, MemberEntity, Package => ScalaPackage}
import collection.mutable.HashMap
import org.scajadoc.util._
import org.scajadoc.extractor.entityQueryContainer

/**
 * Generate deprecated-list.html file.
 *
 * @author Filip Rogaczewski
 * @version 0.9
 */
class DeprecatedListPage(val rootPackage : ScalaPackage) extends HtmlPage {

	import entityQueryContainer._

	/**
	 * Returns true if entity is deprecated. 
	 */
	val deprecated : (MemberEntity => Boolean) =
		(t : MemberEntity) => t.deprecation match {
			case Some(b) => true
			case None => false
		}

	/**
	 * Returns true if entity is a deprecated class.
	 * Deprecated classes are templates which are deprecated and are not:
	 *  - interfaces
	 *  - packages
	 *  - annotations
	 *  - enums
	 */
	val collectDeprecatedClasses : (MemberEntity => Boolean) =
		(t : MemberEntity) => (deprecated(t)
			&& t.isTemplate
			&& !t.asInstanceOf[DocTemplateEntity].isPackage
			&& !t.asInstanceOf[DocTemplateEntity].isTrait
			&& !isException(t.asInstanceOf[DocTemplateEntity])
			&& !isAnnotation(t.asInstanceOf[DocTemplateEntity])
			&& !isEnumeration(t.asInstanceOf[DocTemplateEntity])
			&& !isError(t.asInstanceOf[DocTemplateEntity]))

	/**
	 * Returns true if the entity is a deprecated field.
	 */
	val collectDeprecatedFields : (MemberEntity => Boolean) =
		(t : MemberEntity) => (deprecated(t)
			&& !isEnumeration(t.inTemplate)
			&& (t.isVal || t.isVar))

	val collectDeprecatedConstructors : (MemberEntity => Boolean) =
		(t : MemberEntity) => t.isConstructor && deprecated(t)

	/**
	 * Returns true if the entity is a deprecated method.
	 */
	val collectDeprecatedMethods : (MemberEntity => Boolean) =
		(t : MemberEntity) => (deprecated(t)
			&& t.isDef
			&& !t.isConstructor
			&& !isAnnotation(t.inTemplate))

	/**
	 * Returns true if the entity is a deprecated trait.
	 */
	val collectDeprecatedInterfaces : (MemberEntity => Boolean) =
		(t : MemberEntity) => (deprecated(t)
				&& t.isInstanceOf[DocTemplateEntity]
				&& t.asInstanceOf[DocTemplateEntity].isTrait)

	/**
	 * Returns true if the entity is deprecated element of an annotation.
	 * Deprecated elements of annotations are defs which are defined within the annotation.
	 */
	val collectDeprecatedAnnotationElements : (MemberEntity => Boolean) =
		(t : MemberEntity) => deprecated(t) && isAnnotation(t.inTemplate)

	/**
	 * Returns true if the entity is a deprecated exception.
	 */
	val collectDeprecatedExceptions : (MemberEntity => Boolean) =
		(t : MemberEntity) => (deprecated(t)
			&& t.isInstanceOf[DocTemplateEntity]
			&& isException(t.asInstanceOf[DocTemplateEntity]))

	/**
	 * Returns true if the entity is a deprecated error.
	 */
	val collectDeprecatedErrors : (MemberEntity => Boolean) =
		(t : MemberEntity) => (deprecated(t)
			&& t.isInstanceOf[DocTemplateEntity]
			&& isException(t.asInstanceOf[DocTemplateEntity]))

	/**
	 * Returns true if the entity is a deprecated annotation.
	 */
	val collectDeprecatedAnnotations : (MemberEntity => Boolean) =
		(t : MemberEntity) => (deprecated(t)
			&& t.isInstanceOf[DocTemplateEntity]
			&& isAnnotation(t.asInstanceOf[DocTemplateEntity]))

	/**
	 * Returns true if the entity is a deprecated enum.
	 */
	val collectDeprecatedEnums : (MemberEntity => Boolean) =
		(t : MemberEntity) => (deprecated(t)
			&& t.isInstanceOf[DocTemplateEntity]
			&& isEnumeration(t.asInstanceOf[DocTemplateEntity]))

	/**
	 * Returns true if the entity is a deprecated enum constant.
	 */
	val collectDeprecatedEnumConstants : (MemberEntity => Boolean) =
		(t : MemberEntity) => (deprecated(t)
			&& t.isVal && isEnumeration(t.inTemplate.asInstanceOf[DocTemplateEntity]))

	/**
	 * Map with deprecated sections and functions for collecting deprecated entities.
	 */
	lazy val deprecatedApi : Map[DeprecatedType, (MemberEntity => Boolean)] = Map(
		(DeprecatedType("class", "Deprecated Classes") -> collectDeprecatedClasses),
		(DeprecatedType("interface","Deprecated Interfaces") -> collectDeprecatedInterfaces),
		(DeprecatedType("exception", "Deprecared Exceptions") -> collectDeprecatedExceptions),
		(DeprecatedType("field", "Deprecated Fields") -> collectDeprecatedFields),
		(DeprecatedType("method", "Deprecated Methods") -> collectDeprecatedMethods),
		(DeprecatedType("constructor", "Deprecated Constructors") -> collectDeprecatedConstructors),
		(DeprecatedType("annotation_type_member", "Deprecated Annotation Type Elements") -> collectDeprecatedAnnotationElements),
		(DeprecatedType("annotation_type", "Deprecated Annotations") -> collectDeprecatedAnnotations),
		(DeprecatedType("enum", "Deprecated Enums") -> collectDeprecatedEnums),
		(DeprecatedType("enum_constant", "Deprecated Enum Constants") -> collectDeprecatedEnumConstants))

	var deprecatedEntities = new HashMap[DeprecatedType, List[MemberEntity]]

	def filename = "deprecated-list"

	def pageTitle = "Deprecated List"

	def headers = ""

	def entity = rootPackage

   object deprecatedHtmlUtils extends NavigationBarHtmlUtil

	def body = {
		for (e <- deprecatedApi.iterator)
			deprecatedEntities += (e._1 -> entityTreeTraverser.collect(rootPackage, e._2))
		var body = Nil:List[Node]
      body ++= deprecatedHtmlUtils.navigationBarHtml(entity, deprecated = true, simpleNavigation = true)
		body ++= header
		for (e <- deprecatedEntities.iterator.filter((e : (DeprecatedType, List[MemberEntity])) => !e._2.isEmpty))
			body ++= deprecatedEntity(e._1, e._2)
		body
	}

	private def deprecatedEntity(typ : DeprecatedType, entities : List[MemberEntity]) = {
		var cls = Nil:List[Node]
		cls ++= <p><a name={typ.anchor}><!-- --></a>
		<table border="1" width="100%" cellpadding="3" cellspacing="0" summary=""><tr bgcolor="#CCCCFF" class="TableHeadingColor"><th align="left" colspan="2"><font size="+2"><b>{typ.heading}</b></font></th></tr>
			{entities.map(deprecatedToHtml(_))}
		</table></p>
		cls
	}

	private def deprecatedToHtml(entity : MemberEntity) =
		<tr bgcolor="white" class="TableRowColor"><td><a href={linkResolver.resolve(entity).get.link(rootPackage)}>{entityPresentationUtil.entityName(entity)}</a> {entityPresentationUtil.bodyToHtml(entity.deprecation.get)}</td></tr>

	/**
	 * Returns page title and menu with deprecated sections. 
	 */
	private def header() =
		<hr />
			<center><h2><b>Deprecated API</b></h2></center>
			<hr/>
			<b>Contents</b>
			<ul>
				{
					for (e <- deprecatedEntities.iterator.filter((e : (DeprecatedType, List[MemberEntity])) => !e._2.isEmpty))
						yield <li><a href={"#" + e._1.anchor}>{e._1.heading}</a></li>
				}
			</ul>


}

case class DeprecatedType(anchor : String, heading : String)