package org.unifycode.gradle.tool;

import java.io.File;
import java.util.Collections;
import org.gradle.api.Project;
import org.gradle.api.plugins.quality.PmdExtension;

/**
 * PMD configuration tool.
 */
public final class PmdTool {
    /**
     * PMD ruleset resource path.
     */
    private static final String RESOURCE = "unifycode/pmd/pmd.xml";

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
    public PmdTool(final Project project, final UnifycodeResources resources) {
        this.project = project;
        this.resources = resources;
    }

    /**
     * New tool configured for the given project.
     *
     * @param project current project.
     */
    public PmdTool(final Project project) {
        this(project, new UnifycodeResources(project));
    }

    /**
     * Applies PMD and configures its ruleset.
     */
    public void configure() {
        this.project.getPluginManager().apply("pmd");
        final File config = this.resources.copy(PmdTool.RESOURCE);
        this.project.getExtensions().configure(PmdExtension.class, extension -> {
            // Keep the PMD version explicit here until tool versions are centralized.
            extension.setToolVersion("7.0.0");
            extension.setConsoleOutput(true);
            extension.setRuleSets(Collections.emptyList());
            extension.setRuleSetFiles(this.project.files(config));
        });
    }
}
