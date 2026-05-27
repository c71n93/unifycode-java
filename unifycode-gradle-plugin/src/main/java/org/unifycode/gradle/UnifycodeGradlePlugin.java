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
        project.getExtensions().create(
            "unifycode",
            UnifycodeExtension.class
        );
        new UnifycodeTasks(project.getTasks()).configure();
        project.getPluginManager().withPlugin("java-base", plugin -> project.afterEvaluate(ignored -> {
            new CheckstyleTool(project).configure();
            new PmdTool(project).configure();
            new SpotlessTool(project).configure();
            project.getTasks().named("check").configure(task -> task.dependsOn("unifycodeCheck"));
        }));
    }
}
