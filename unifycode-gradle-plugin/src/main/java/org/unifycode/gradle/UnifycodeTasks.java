package org.unifycode.gradle;

import java.util.Collections;
import java.util.concurrent.Callable;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.quality.Checkstyle;
import org.gradle.api.plugins.quality.Pmd;

/**
 * UnifyCode helper task registration.
 */
final class UnifycodeTasks {
    /**
     * Current project.
     */
    private final Project project;

    /**
     * Plugin extension.
     */
    private final UnifycodeExtension extension;

    /**
     * New helper task registration.
     *
     * @param project current project.
     * @param extension plugin extension.
     */
    public UnifycodeTasks(final Project project, final UnifycodeExtension extension) {
        this.project = project;
        this.extension = extension;
    }

    /**
     * Configure helper tasks.
     */
    public void configure() {
        this.project.getTasks().register("unifycodeFormat", task -> {
            task.setGroup("formatting");
            task.setDescription("Runs source formatting via Spotless.");
            task.dependsOn((Callable<Iterable<? extends Task>>) this::spotlessApplyTasks);
        });
        this.project.getTasks().register("unifycodeCheck", task -> {
            task.setGroup("verification");
            task.setDescription("Runs static analysis checks without launching tests.");
            task.dependsOn((Callable<Iterable<? extends Task>>) this::spotlessCheckTasks);
            task.dependsOn(this.checkstyleTasks());
            task.dependsOn(this.pmdTasks());
        });
    }

    private Iterable<? extends Task> spotlessApplyTasks() {
        return this.taskNamedWhenEnabled("spotlessApply", this.extension.getSpotlessEnabled().get());
    }

    private Iterable<? extends Task> spotlessCheckTasks() {
        return this.taskNamedWhenEnabled("spotlessCheck", this.extension.getSpotlessEnabled().get());
    }

    private Iterable<? extends Task> checkstyleTasks() {
        return this.tasksTypedWhenEnabled(Checkstyle.class, this.extension.getCheckstyleEnabled().get());
    }

    private Iterable<? extends Task> pmdTasks() {
        return this.tasksTypedWhenEnabled(Pmd.class, this.extension.getPmdEnabled().get());
    }

    // @checkstyle ReturnCount (8 lines)
    private Iterable<? extends Task> taskNamedWhenEnabled(final String name, final boolean enabled) {
        if (!enabled) {
            return Collections.emptyList();
        }
        final Task task = this.project.getTasks().findByName(name);
        if (task == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(task);
    }

    private <T extends Task> Iterable<T> tasksTypedWhenEnabled(final Class<T> type, final boolean enabled) {
        if (!enabled) {
            return Collections.emptyList();
        }
        return this.project.getTasks().withType(type);
    }
}
