package org.scajadoc.frontend.page

import xml.Node
import org.scajadoc.frontend.{entityPresentationUtil, entityTreeTraverser}
import tools.nsc.doc.model.{DocTemplateEntity, MemberEntity, Package => ScalaPackage}
import collection.mutable.HashMap

/**
 * Generate deprecated-list.html file.
 *
 * @author Filip Rogaczewski
 */
class DeprecatedListPage(val rootPackage : ScalaPackage) extends HtmlPage {

	/**
	 * Returns true if entity is deprecated. 
	 */
	val deprecated : (MemberEntity => Boolean) =
		(t : MemberEntity) => t.deprecation match {
			case Some(b) => true
			case None => false
		}

	/**
	 * Returns true if entity is an exception.
	 */
	val isException : (DocTemplateEntity => Boolean) =
		(t : DocTemplateEntity) => t.linearizationTemplates.exists(_.qualifiedName == classOf[Exception].getCanonicalName)

	/**
	 * Returns true if entity is an error.
	 */
	val isError : (DocTemplateEntity => Boolean) =
		(t : DocTemplateEntity) => t.linearizationTemplates.exists(_.qualifiedName == classOf[Error].getCanonicalName)

	/**
	 * Returns true if entity is a deprecated class.
	 * Deprecated classes are templates which are deprecated and are not:
	 *  - interfaces
	 *  - packages
	 *  - annotations
	 *  - enums
	 */
	val collectDeprecatedClasses : (MemberEntity => Boolean) =
		(t : MemberEntity) => (t.isTemplate
			&& !t.asInstanceOf[DocTemplateEntity].isPackage
			&& !isException(t.asInstanceOf[DocTemplateEntity])
			&& deprecated(t))

	/**
	 * Returns true if entity is deprecated field.
	 */
	val collectDeprecatedFields : (MemberEntity => Boolean) =
		(t : MemberEntity) => ((t.isVal || t.isVar) && deprecated(t))

	val collectDeprecatedConstructors : (MemberEntity => Boolean) =
		(t : MemberEntity) => t.isConstructor && deprecated(t)

	/**
	 * Returns true if entity is deprecated method.
	 */
	val collectDeprecatedMethods : (MemberEntity => Boolean) =
		(t : MemberEntity) => (deprecated(t) && t.isDef)

	/**
	 * TODO implement.
	 */
	val collectDeprecatedAnnotationElements : (MemberEntity => Boolean) =
		(t : MemberEntity) => false && deprecated(t)

	/**
	 * TODO implement.
	 */
	val collectDeprecatedInterfaces : (MemberEntity => Boolean) =
		(t : MemberEntity) => (false && deprecated(t))

	/**
	 * Returns true if entity is deprecated exception.
	 */
	val collectDeprecatedExceptions : (MemberEntity => Boolean) =
		(t : MemberEntity) => (deprecated(t)
			&& t.isInstanceOf[DocTemplateEntity]
			&& isException(t.asInstanceOf[DocTemplateEntity]))

	/**
	 * Returns true if entity is deprecated error.
	 */
	val collectDeprecatedErrors : (MemberEntity => Boolean) =
		(t : MemberEntity) => deprecated(t) && false

	/**
	 * TODO implement.
	 */
	val collectDeprecatedAnnotations : (MemberEntity => Boolean) =
		(t : MemberEntity) => deprecated(t) && false

	/**
	 * TODO implement.
	 */
	val collectDeprecatedEnums : (MemberEntity => Boolean) =
		(t : MemberEntity) => deprecated(t) && false

	val collectDeprecatedEnumConstants : (MemberEntity => Boolean) =
		(t : MemberEntity) => deprecated(t) && false
	
	lazy val deprecatedApi : Map[DeprecatedType, (MemberEntity => Boolean)] =
		Map((DeprecatedType("class", "Deprecated Classes") -> collectDeprecatedClasses),
			(DeprecatedType("interface","Deprecated Interfaces") -> collectDeprecatedInterfaces),
			(DeprecatedType("exception", "Deprecared Exceptions") -> collectDeprecatedExceptions),
			(DeprecatedType("field", "Deprecated Fields") -> collectDeprecatedFields),
			(DeprecatedType("method", "Deprecated Methods") -> collectDeprecatedMethods),
			(DeprecatedType("constructor", "Deprecated Constructors") -> collectDeprecatedConstructors),
			(DeprecatedType("annotation_type_member", "Deprecated Annotation Type Elements") -> collectDeprecatedAnnotationElements),
			(DeprecatedType("annotation_type", "Deprecated Annotations") -> collectDeprecatedAnnotations),
			(DeprecatedType("enum", "Deprecated Enums") -> collectDeprecatedEnums),
			(DeprecatedType("enum_constant", "Deprecated Enum Constants") -> collectDeprecatedEnumConstants)
		)

	var deprecatedEntities = new HashMap[DeprecatedType, List[MemberEntity]]

	def filename = "deprecated-list.html"

	def pageTitle = "Deprecated List"

	def headers = ""

	def entity = rootPackage

	def body = {
		for (e <- deprecatedApi.iterator)
			deprecatedEntities += (e._1 -> entityTreeTraverser.collect(rootPackage, e._2))
		var body = Nil:List[Node]
		body ++= header
		for (e <- deprecatedEntities.iterator.filter((e : (DeprecatedType, List[MemberEntity])) => !e._2.isEmpty))
			body ++= deprecatedEntity(e._1, e._2)
		body
	}

	private def deprecatedEntity(typ : DeprecatedType, entities : List[MemberEntity]) = {
		var cls = Nil:List[Node]
		cls ++= <a name={typ.anchor}><!-- --></a>
		cls ++= <table border="1" width="100%" cellpadding="3" cellspacing="0" summary=""><tr bgcolor="#CCCCFF" class="TableHeadingColor"><th align="left" colspan="2"><font size="+2"><b>{typ.heading}</b></font></th></tr>
			{entities.map(deprecatedToHtml(_))}
		</table>
		cls
	}

	private def deprecatedToHtml(entity : MemberEntity) =
		<tr bgcolor="white" class="TableRowColor"><td>{entity.name} {entityPresentationUtil.bodyToHtml(entity.deprecation.get)}</td></tr>

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