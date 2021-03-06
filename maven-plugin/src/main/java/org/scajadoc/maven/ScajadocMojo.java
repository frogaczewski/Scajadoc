package org.scajadoc.maven;

import org.scajadoc.Scajadoc;

import java.io.File;
import java.util.List;

/**
 * Entry point for scajadoc maven plugin.
 *
 *
 * @author Filip Rogaczewski
 * @goal run
 * @execute phase="generate-sources"
 */
public class ScajadocMojo extends AbstractScajadocMojo {

   /**
    * The directory in which to place output of scajadoc run.
    *
    * @parameter expression="${project.build.directory}"
    */
    protected File outputDir;

	/**
	 * @parameter
	 */
	private String destination;

   /**
    * List of external APIs.
    *
    * @parameter
    */
   private String[] links;

	/**
	 * @parameter
	 */
	private String scalaVersion = "2.8.1";

	@Override
	protected void doExecute() throws Exception {
		Scajadoc.build(
				getSourcepath(),
				getOutputDir().getAbsolutePath(),
				getClasspath(),
				getProjectName(),
            getLinks()
		);
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getScalaVersion() {
		return scalaVersion;
	}

	public void setScalaVersion(String scalaVersion) {
		this.scalaVersion = scalaVersion;
	}
   public File getOutputDir() {
      return outputDir;
   }

   public void setOutputDir(File outputDir) {
      this.outputDir = outputDir;
   }

   public String[] getLinks() {
      return links;
   }

   public void setLinks(String[] links) {
      this.links = links;
   }
}
