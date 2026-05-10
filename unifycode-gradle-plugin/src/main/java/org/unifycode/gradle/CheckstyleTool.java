package org.unifycode.gradle;

import java.io.File;
import org.gradle.api.Project;
import org.gradle.api.plugins.quality.CheckstyleExtension;

final class CheckstyleTool {
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

    CheckstyleTool(final Project project, final UnifycodeResources resources) {
        this.project = project;
        this.resources = resources;
    }

    CheckstyleTool(final Project project) {
        this(project, new UnifycodeResources(project));
    }

    void configure() {
        this.project.getPluginManager().apply("checkstyle");
        final File config = this.resources.copy(CheckstyleTool.RESOURCE);
        this.project.getExtensions().configure(CheckstyleExtension.class, extension -> extension.setConfigFile(config));
    }
}
