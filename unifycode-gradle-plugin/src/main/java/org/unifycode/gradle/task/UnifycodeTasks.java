package org.unifycode.gradle.task;

import java.util.Collections;
import java.util.concurrent.Callable;
import org.gradle.api.Task;
import org.gradle.api.plugins.quality.Checkstyle;
import org.gradle.api.plugins.quality.Pmd;
import org.gradle.api.tasks.TaskContainer;
import org.unifycode.gradle.UnifycodeExtension;

/**
 * UnifyCode helper task registration.
 */
public final class UnifycodeTasks {
    /**
     * Gradle task container.
     */
    private final TaskContainer tasks;

    /**
     * Plugin extension.
     */
    private final UnifycodeExtension extension;

    /**
     * New helper task registration.
     *
     * @param tasks gradle task container.
     * @param extension plugin extension.
     */
    public UnifycodeTasks(final TaskContainer tasks, final UnifycodeExtension extension) {
        this.tasks = tasks;
        this.extension = extension;
    }

    /**
     * Configure helper tasks.
     *
     * @todo: #2:30min Make unifycodeFormat and unifycodeCheck fields instead of hard coded values.
     */
    public void configure() {
        this.tasks.register("unifycodeFormat", task -> {
            task.setGroup("formatting");
            task.setDescription("Runs source formatting via Spotless.");
            task.dependsOn((Callable<Iterable<? extends Task>>) this::spotlessApplyTasks);
        });
        this.tasks.register("unifycodeCheck", task -> {
            task.setGroup("verification");
            task.setDescription("Runs static analysis checks without launching tests.");
            task.dependsOn((Callable<Iterable<? extends Task>>) this::spotlessCheckTasks);
            task.dependsOn((Callable<Iterable<? extends Task>>) this::checkstyleTasks);
            task.dependsOn((Callable<Iterable<? extends Task>>) this::pmdTasks);
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
        final Task task = this.tasks.findByName(name);
        if (task == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(task);
    }

    private <T extends Task> Iterable<T> tasksTypedWhenEnabled(final Class<T> type, final boolean enabled) {
        if (!enabled) {
            return Collections.emptyList();
        }
        return this.tasks.withType(type);
    }
}
