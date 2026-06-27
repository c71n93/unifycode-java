package io.github.c71n93.unifycode.gradle.tool;

import io.github.c71n93.unifycode.gradle.extension.QualityToolPolicy;
import io.github.c71n93.unifycode.gradle.extension.UnifycodeExtension;
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
    private static final String RESOURCE = "io/github/c71n93/unifycode/checkstyle/checkstyle.xml";

    /**
     * Checkstyle suppression configuration resource path.
     */
    private static final String SUPPRESSIONS = "io/github/c71n93/unifycode/checkstyle/checkstyle-suppressions.xml";

    /**
     * Project facade.
     */
    private final ToolProject project;

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
     * @param project project facade.
     * @param resources resource copier.
     * @param policy checkstyle policy.
     */
    public CheckstyleTool(
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
    public CheckstyleTool(final Project project) {
        this(
            new ToolProject(project),
            new UnifycodeResources(project),
            project.getExtensions().getByType(UnifycodeExtension.class).getCheckstyle()
        );
    }

    /**
     * Applies Checkstyle and configures its ruleset.
     */
    public void configure() {
        this.project.applyPlugin("checkstyle");
        final File config = this.resources.copy(CheckstyleTool.RESOURCE);
        final File suppressions = this.resources.copy(CheckstyleTool.SUPPRESSIONS);
        // @todo #2:30min Pin Checkstyle toolVersion to an up-to-date release instead of relying on Gradle defaults.
        this.project.configureExtension(CheckstyleExtension.class, extension -> {
            extension.setConfigFile(config);
            extension.setIgnoreFailures(this.policy.ignoresFailures());
            extension.getConfigProperties().put("unifycode.checkstyle.suppressions", suppressions.getAbsolutePath());
        });
    }
}
