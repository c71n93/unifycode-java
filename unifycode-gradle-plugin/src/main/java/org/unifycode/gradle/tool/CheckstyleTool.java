package org.unifycode.gradle.tool;

import java.io.File;
import org.gradle.api.Project;
import org.gradle.api.plugins.quality.CheckstyleExtension;

/**
 * Checkstyle configuration tool.
 */
public final class CheckstyleTool {
    /**
     * Checkstyle configuration resource path.
     */
    private static final String RESOURCE = "unifycode/checkstyle/checkstyle.xml";

    /**
     * Current project.
     */
    private final Project project;

    /**
     * Resource copier.
     */
    private final UnifycodeResources resources;

    /**
     * New tool configured with an explicit resource copier.
     *
     * @param project current project.
     * @param resources resource copier.
     */
    public CheckstyleTool(final Project project, final UnifycodeResources resources) {
        this.project = project;
        this.resources = resources;
    }

    /**
     * New tool configured for the given project.
     *
     * @param project current project.
     */
    public CheckstyleTool(final Project project) {
        this(project, new UnifycodeResources(project));
    }

    /**
     * Applies Checkstyle and configures its ruleset.
     */
    public void configure() {
        this.project.getPluginManager().apply("checkstyle");
        final File config = this.resources.copy(CheckstyleTool.RESOURCE);
        this.project.getExtensions().configure(CheckstyleExtension.class, extension -> extension.setConfigFile(config));
    }
}
