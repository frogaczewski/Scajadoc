package org.scajadoc.maven;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Abstract mojo with helper methods and encapsulation of injected parameters. 
 *
 * @author Filip Rogaczewski
 */
public abstract class AbstractScajadocMojo extends AbstractMojo {

	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

	/**
	 * Used to lookup artifacts in remote repositories.
	 *
	 * @component
	 * @required
	 * @readonly
	 */
	protected ArtifactResolver resolver;

	/**
	 * Factory used to lookup artifacts in the remote repository.
	 *
	 * @component
	 * @required
	 * @readonly
	 */
	protected ArtifactFactory factory;

	/**
	 * @parameter expression="${project.remoteArtifactRepositories}"
	 * @readonly
	 * @required
	 */
	protected List<?> remoteRepos;

	/**
	 * @readonly
	 * @required
	 * @parameter expression="${localRepository}"
	 */
	protected ArtifactRepository localRepo;

	/**
	 * @component
	 * @required
	 * @readonly
	 */
	protected MavenProjectBuilder mavenProjectBuilder;

	/**
	 * @component
	 * @required
	 * @readonly
	 */
	protected DependencyTreeBuilder dependencyTreeBuilder;

	/**
	 * @component
	 * @required
	 * @readonly
	 */
	protected ArtifactCollector artifactCollector;

	/**
	 * @component
	 * @required
	 * @readonly
	 */
	protected ArtifactMetadataSource artifactMetadataSource;

	private static final ArtifactFilter filter = new ArtifactFilter() {
		public boolean include(Artifact artifact) {
			return !artifact.isOptional();
		}
	};

	/**
	 * Mojo's execution.
	 *
	 * @throws org.apache.maven.plugin.MojoExecutionException
	 * @throws org.apache.maven.plugin.MojoFailureException
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			doExecute();
		} catch (Exception e) {
			getLog().error(e);
		}
	}

	protected abstract void doExecute() throws Exception;

	protected Set<String> getClasspath() throws Exception {
		DependencyNode node = dependencyTreeBuilder.buildDependencyTree(project, localRepo, factory,
				artifactMetadataSource, filter, artifactCollector);
		Iterator<DependencyNode> nodeIterator = node.preorderIterator();
		Set<String> classpath = new HashSet<String>();
		while (nodeIterator.hasNext()) {
			Artifact a = nodeIterator.next().getArtifact();
			resolver.resolve(a, remoteRepos, localRepo);
			classpath.add(a.getFile().getAbsolutePath());
		}
		return classpath;
	}

	protected String getSourcepath() {
		return project.getBasedir().toString();
	}

	protected String getProjectName() {
		return project.getName();
	}

}
