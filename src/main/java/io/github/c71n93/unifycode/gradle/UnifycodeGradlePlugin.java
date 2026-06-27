package io.github.c71n93.unifycode.gradle;

import io.github.c71n93.unifycode.gradle.extension.UnifycodeExtension;
import io.github.c71n93.unifycode.gradle.task.UnifycodeTasks;
import io.github.c71n93.unifycode.gradle.tool.CheckstyleTool;
import io.github.c71n93.unifycode.gradle.tool.PmdTool;
import io.github.c71n93.unifycode.gradle.tool.SpotlessTool;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

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
        project.getPluginManager().withPlugin("java-base", plugin -> {
            new UnifycodeTasks(project.getTasks()).configure();
            project.afterEvaluate(ignored -> {
                new CheckstyleTool(project).configure();
                new PmdTool(project).configure();
                new SpotlessTool(project).configure();
                project.getTasks().named("check").configure(task -> task.dependsOn("unifycodeCheck"));
            });
        });
    }
}
