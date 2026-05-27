package org.unifycode.gradle.tool;

import java.io.File;
import org.gradle.api.Project;
import org.gradle.api.plugins.quality.CheckstyleExtension;
import org.unifycode.gradle.QualityToolPolicy;

/**
 * Checkstyle configuration tool.
 */
public final class CheckstyleTool {
    /**
     * Checkstyle configuration resource path.
     */
    private static final String RESOURCE = "unifycode/checkstyle/checkstyle.xml";

    /**
     * Checkstyle suppression configuration resource path.
     */
    private static final String SUPPRESSIONS = "unifycode/checkstyle/checkstyle-suppressions.xml";

    /**
     * Current project.
     */
    private final Project project;

    /**
     * Resource copier.
     */
    private final UnifycodeResources resources;

    /**
     * Checkstyle policy.
     */
    private final QualityToolPolicy policy;

    /**
     * New tool configured with an explicit resource copier.
     *
     * @param project current project.
     * @param resources resource copier.
     * @param policy checkstyle policy.
     */
    public CheckstyleTool(
                          final Project project,
                          final UnifycodeResources resources,
                          final QualityToolPolicy policy) {
        this.project = project;
        this.resources = resources;
        this.policy = policy;
    }

    /**
     * New tool configured with an explicit policy.
     *
     * @param project current project.
     * @param policy checkstyle policy.
     */
    public CheckstyleTool(final Project project, final QualityToolPolicy policy) {
        this(project, new UnifycodeResources(project), policy);
    }

    /**
     * New tool configured for the given project.
     *
     * @param project current project.
     */
    public CheckstyleTool(final Project project) {
        this(
            project,
            new UnifycodeResources(project),
            project.getExtensions().getByType(org.unifycode.gradle.UnifycodeExtension.class).getCheckstyle()
        );
    }

    /**
     * Applies Checkstyle and configures its ruleset.
     */
    public void configure() {
        this.project.getPluginManager().apply("checkstyle");
        final File config = this.resources.copy(CheckstyleTool.RESOURCE);
        final File suppressions = this.resources.copy(CheckstyleTool.SUPPRESSIONS);
        // @todo #2:30min Pin Checkstyle toolVersion to an up-to-date release instead of relying on Gradle defaults.
        this.project.getExtensions().configure(CheckstyleExtension.class, extension -> {
            extension.setConfigFile(config);
            extension.setIgnoreFailures(this.policy.ignoresFailures());
            extension.getConfigProperties().put("unifycode.checkstyle.suppressions", suppressions.getAbsolutePath());
        });
    }
}
