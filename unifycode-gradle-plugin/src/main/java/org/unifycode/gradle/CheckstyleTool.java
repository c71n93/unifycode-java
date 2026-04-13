package org.unifycode.gradle;

import java.io.File;
import org.gradle.api.Project;
import org.gradle.api.plugins.quality.CheckstyleExtension;

final class CheckstyleTool {
    private static final String RESOURCE = "unifycode/checkstyle/checkstyle.xml";

    private final Project project;
    private final UnifycodeResources resources;

    CheckstyleTool(final Project project, final UnifycodeResources resources) {
        this.project = project;
        this.resources = resources;
    }

    CheckstyleTool(final Project project) {
        this.project = project;
        this.resources = new UnifycodeResources(project);
    }

    void configure() {
        this.project.getPluginManager().apply("checkstyle");
        final File config = this.resources.copy(CheckstyleTool.RESOURCE);
        this.project.getExtensions().configure(CheckstyleExtension.class, extension -> extension.setConfigFile(config));
    }
}
