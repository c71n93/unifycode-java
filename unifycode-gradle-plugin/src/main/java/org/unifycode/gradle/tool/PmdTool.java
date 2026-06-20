package org.unifycode.gradle.tool;

import java.io.File;
import java.util.Collections;
import org.gradle.api.Project;
import org.gradle.api.plugins.quality.PmdExtension;
import org.unifycode.gradle.extension.QualityToolPolicy;
import org.unifycode.gradle.extension.UnifycodeExtension;

/**
 * PMD configuration tool.
 */
public final class PmdTool {
    /**
     * PMD ruleset resource path.
     */
    private static final String RESOURCE = "unifycode/pmd/pmd.xml";

    /**
     * Project facade.
     */
    private final ToolProject project;

    /**
     * Resource copier.
     */
    private final UnifycodeResources resources;

    /**
     * PMD policy.
     */
    private final QualityToolPolicy policy;

    /**
     * New tool configured with an explicit resource copier.
     *
     * @param project project facade.
     * @param resources resource copier.
     * @param policy tool policy.
     */
    public PmdTool(
                   final ToolProject project,
                   final UnifycodeResources resources,
                   final QualityToolPolicy policy) {
        this.project = project;
        this.resources = resources;
        this.policy = policy;
    }

    /**
     * New tool configured for the given project.
     *
     * @param project current project.
     */
    public PmdTool(final Project project) {
        this(
            new ToolProject(project),
            new UnifycodeResources(project),
            project.getExtensions().getByType(UnifycodeExtension.class).getPmd()
        );
    }

    /**
     * Applies PMD and configures its ruleset.
     */
    public void configure() {
        this.project.applyPlugin("pmd");
        final File config = this.resources.copy(PmdTool.RESOURCE);
        this.project.configureExtension(PmdExtension.class, extension -> {
            // @todo #2:40min Make all tool versions configurable from one place. For example from UnifycodeExtension
            // class.
            extension.setToolVersion("7.0.0");
            extension.setConsoleOutput(true);
            extension.setIgnoreFailures(this.policy.ignoresFailures());
            extension.setRuleSets(Collections.emptyList());
            extension.setRuleSetFiles(this.project.files(config));
        });
    }
}
