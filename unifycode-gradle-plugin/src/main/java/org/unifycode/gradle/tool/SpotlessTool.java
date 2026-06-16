package org.unifycode.gradle.tool;

import com.diffplug.gradle.spotless.SpotlessExtension;
import java.io.File;
import org.gradle.api.Project;

/**
 * Spotless configuration tool.
 */
public final class SpotlessTool {
    /**
     * Spotless formatter resource path.
     */
    private static final String RESOURCE = "unifycode/spotless/eclipse-java-formatter.xml";

    /**
     * Project facade.
     */
    private final ToolProject project;

    /**
     * Resource copier.
     */
    private final UnifycodeResources resources;

    /**
     * New tool configured with an explicit project facade and resource copier.
     *
     * @param project project facade.
     * @param resources resource copier.
     */
    public SpotlessTool(final ToolProject project, final UnifycodeResources resources) {
        this.project = project;
        this.resources = resources;
    }

    /**
     * New tool configured for the given project.
     *
     * @param project current project.
     */
    public SpotlessTool(final Project project) {
        this(new ToolProject(project), new UnifycodeResources(project));
    }

    /**
     * Applies Spotless and configures its formatter.
     */
    public void configure() {
        this.project.applyPlugin("com.diffplug.spotless");
        final File config = this.resources.copy(SpotlessTool.RESOURCE);
        this.project.configureExtension(SpotlessExtension.class, extension -> extension.java(java -> {
            java.eclipse().configFile(config);
        }));
    }
}
