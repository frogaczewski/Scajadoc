package org.scajadoc.util

import tools.nsc.doc.model.{MemberEntity, DocTemplateEntity}

/**
 * Utility object with methods for traversing entity tree. 
 *
 * @author Filip Rogaczewski
 */
object entityTreeTraverser {

	/**
	 * Execute the function while traversing tree of entities.  
	 */
	def traverse(doc : DocTemplateEntity, executable: DocTemplateEntity => Unit) : Unit = {
		executable(doc)
		doc.templates.map(traverse(_, executable))
	}

	/**
	 * Collects all entities matching the query.
	 */
	def collect(doc : DocTemplateEntity, query : MemberEntity => Boolean) : List[MemberEntity] = {
		var result = doc.members.filter(query)
		for (template <- doc.templates)
			result :::= collect(template, query)
		result
	}

}