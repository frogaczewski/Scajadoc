package org.scajadoc.maven;

import org.scajadoc.Scajadoc;

/**
 * Entry point for scajadoc maven plugin.
 *
 * @author Filip Rogaczewski
 * @goal scajadoc
 */
public class ScajadocMojo extends AbstractScajadocMojo {
	
	/**
	 * @parameter
	 */
	private String destination;

	/**
	 * @parameter
	 */
	private String scalaVersion = "2.8.1";

	@Override
	protected void doExecute() throws Exception {
		Scajadoc.build(
				getSourcepath(),
				getDestination(),
				getClasspath(),
				getProjectName()
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
}
