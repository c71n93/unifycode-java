package org.unifycode.gradle;

import com.diffplug.gradle.spotless.SpotlessExtension;
import java.io.File;
import org.gradle.api.Project;

final class SpotlessTool {
    /**
     * Spotless formatter resource path.
     */
    private static final String RESOURCE = "unifycode/spotless/eclipse-java-formatter.xml";

    /**
     * Current project.
     */
    private final Project project;

    /**
     * Resource copier.
     */
    private final UnifycodeResources resources;

    SpotlessTool(final Project project, final UnifycodeResources resources) {
        this.project = project;
        this.resources = resources;
    }

    SpotlessTool(final Project project) {
        this(project, new UnifycodeResources(project));
    }

    void configure() {
        this.project.getPluginManager().apply("com.diffplug.spotless");
        final File config = this.resources.copy(SpotlessTool.RESOURCE);
        this.project.getExtensions().configure(SpotlessExtension.class, extension -> extension.java(java -> {
            java.eclipse().configFile(config);
        }));
    }
}
