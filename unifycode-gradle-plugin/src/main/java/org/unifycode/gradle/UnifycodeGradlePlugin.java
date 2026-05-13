package org.unifycode.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.unifycode.gradle.task.UnifycodeTasks;
import org.unifycode.gradle.tool.CheckstyleTool;
import org.unifycode.gradle.tool.PmdTool;
import org.unifycode.gradle.tool.SpotlessTool;

/**
 * Gradle plugin entry point for Unifycode quality tooling.
 */
public final class UnifycodeGradlePlugin implements Plugin<Project> {
    @Override
    public void apply(final Project project) {
        final UnifycodeExtension extension = project.getExtensions().create(
            "unifycode",
            UnifycodeExtension.class
        );
        new UnifycodeTasks(project, extension).configure();
        project.getPluginManager().withPlugin("java-base", plugin -> project.afterEvaluate(ignored -> {
            if (extension.getCheckstyleEnabled().get()) {
                new CheckstyleTool(project).configure();
            }
            if (extension.getPmdEnabled().get()) {
                new PmdTool(project).configure();
            }
            if (extension.getSpotlessEnabled().get()) {
                new SpotlessTool(project).configure();
            }
            project.getTasks().named("check").configure(task -> task.dependsOn("unifycodeCheck"));
        }));
    }
}
