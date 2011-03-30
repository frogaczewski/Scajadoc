package org.scajadoc.frontend

import xml.NodeSeq
import tools.nsc.doc.model.comment._
import tools.nsc.doc.model._
import tools.nsc.doc.model.comment.{Link => ModelLink}

/**
 * Utility class with methods for presenting entity on html page. 
 *
 * @author Filip Rogaczewski
 */
object entityPresentationUtil {

	def bodyToHtml(body : Body) : NodeSeq = body.blocks flatMap (blockToHtml(_))

	private def blockToHtml(block : Block) : NodeSeq = block match {
		case Title(in, 1) => <h3>{ inlineToHtml(in) }</h3>
		case Title(in, 2) => <h4>{ inlineToHtml(in) }</h4>
		case Title(in, 3) => <h5>{ inlineToHtml(in) }</h5>
		case Title(in, _) => <h6>{ inlineToHtml(in) }</h6>
		case Paragraph(in) => <p>{ inlineToHtml(in) }</p>
		case Code(data) => <pre>{ xml.Text(data) }</pre>
		case UnorderedList(items) =>
			<ul>{ /*listItemsToHtml(items)*/ }</ul>
		case OrderedList(items, listStyle) =>
			<b></b> /*<ol class={ listStyle }>{ listItemsToHtml(items) }</ol> */
		case DefinitionList(items) => <b></b>
			/*<dl>{items map { case (t, d) => <dt>{ inlineToHtml(t) }</dt><dd>{ blockToHtml(d) }</dd> } }</dl> */
		case HorizontalRule() =>
				<hr/>
	}

	def inlineToHtml(inl: Inline): NodeSeq = inl match {
		case Chain(items) => items flatMap (inlineToHtml(_))
		case Italic(in) => <i>{ inlineToHtml(in) }</i>
		case Bold(in) => <b>{ inlineToHtml(in) }</b>
		case Underline(in) => <u>{ inlineToHtml(in) }</u>
		case Superscript(in) => <sup>{ inlineToHtml(in) }</sup>
		case Subscript(in) => <sub>{ inlineToHtml(in) }</sub>
		case ModelLink(raw, title) => <a href={ raw }>{ inlineToHtml(title) }</a>
		case EntityLink(entity) => {null}
		case Monospace(text) => <code>{ xml.Text(text) }</code>
		case Text(text) => xml.Text(text)
		case Summary(in) => inlineToHtml(in)
		case HtmlTag(tag) => xml.Unparsed(tag)
	}

	/**
	 * Returns true if the definition should be documented.
	 */
	def isDocumentable(definition : Def) = definition.inDefinitionTemplates.contains(definition.inTemplate)


	/**
	 * Returns short summary of comment.
	 *
	 * @param comment - comment option
	 * @return first sentence of comment
	 */
	def short(comment : Option[Comment]) = comment match {
		case Some(comment) => entityPresentationUtil.inlineToHtml(comment.short).text
		case None => ""
	}


	/**
	 * Returns parameters of the entity.
	 * TODO generic handling
	 */
	def params(executable : NonTemplateMemberEntity) = {
		executable match {
			case d : Def => methodParams(d.valueParams)
			case c : Constructor => methodParams(c.valueParams)
			case _ => ""
		}
	}

	/**
	 * Returns list of params.
	 *
	 * TODO add generic handling
	 */
	def methodParams(valueParams : List[List[ValueParam]]) = {
		var paramsTypes = Nil:List[String]
		valueParams.foreach(params => params.foreach(
				param => paramsTypes ::= param.resultType.name
			)
		)
		"(" + paramsTypes.mkString(",") + ")"
	}

	/**
	 * Returns type of the template entity.
	 *
	 * TODO add exception handling
	 * TODO add annotation handling
	 * TODO enum handling
	 */
	def templateType(template : DocTemplateEntity) = {
		if (template.isPackage)
			"Package"
		else if (template.isTrait)
			"Interface"
		else
			"Class"
	}

	/**
	 * Returns type of the method. Static or instance.
	 */
	def methodType(definition : Def) = {
		if (definition.inTemplate.isObject)
			"Static method"
		else
			"Method"
	}

	/**
	 * Returns type of the variable. Static or instance.
	 */
	def variableType(variable : Val) = {
		if (variable.inTemplate.isObject)
			"Static variable"
		else
			"Variable"
	}

	def inType(entity : MemberEntity) = templateType(entity.inTemplate).toLowerCase

	def inPackage(typ : DocTemplateEntity) = classpathCache(typ).packageCanonicalPath

	/**
	 * Returns name of the entity. In case of methods and constructors appends parameters
	 * to the name.
	 */
	def entityName(entity : MemberEntity) = {
		if (entity.isConstructor || entity.isDef)
			entity.name + params(entity.asInstanceOf[NonTemplateMemberEntity])
		else
			entity.name
	}

}