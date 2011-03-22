package org.scajadoc.frontend.page

import tools.nsc.doc.model.DocTemplateEntity
import java.io.File

/**
 * Trait for writers which create file with documentable entities. 
 *
 * @author Filip Rogaczewski
 */
@Deprecated()
trait FileConstruct {

	/**
	 * File extension.
	 */
	private val extension = ".html"

//	val directory = settings.outdir.value

	/**
	 * Creates file structure for the entity.
	 *
	 * @param entity - entity for which file structure is created.
	 * @return created file. 
	 *
	def createFileStructure(entity : DocTemplateEntity) : File = {
		val pageFile = new File(directory, pagePath(entity))
		val pageDirectory = pageFile.getParentFile
		if (!pageDirectory.exists)
			pageDirectory.mkdirs
		if (!pageFile.exists && !entity.isPackage)
			pageFile.createNewFile
		else if (!pageFile.exists && entity.isPackage)
			pageFile.mkdir
		pageFile
	}

	/**
	 * Creates file.
	 *
	 * @param dir - directory in which the file should be created
	 * @param filename - name of the file
	 * @return file
	 */
	def createFile(dir : String, filename : String) : File = {
		val file = new File(dir, filename)
		if (!file.exists)
			file.createNewFile
		file
	}

	/**
	 * @return doc's file path.
	 */
	private def pagePath(entity : DocTemplateEntity) : String = {
		val path = new StringBuilder
		entity.toRoot.reverse.foreach((d : DocTemplateEntity) => {
			if (d.isRootPackage) {
				path.append("api/")
			} else if (d.isPackage) {
				path.append(d.name)
				path.append(File.separator)
			} else if (!d.isPackage) {
				path.append(d.name)
				path.append(extension)
			}
		})
		path.toString
	} */

}