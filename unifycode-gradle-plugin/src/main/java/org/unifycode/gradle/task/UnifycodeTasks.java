package org.unifycode.gradle.task;

import java.util.Collections;
import java.util.concurrent.Callable;
import org.gradle.api.Task;
import org.gradle.api.plugins.quality.Checkstyle;
import org.gradle.api.plugins.quality.Pmd;
import org.gradle.api.tasks.TaskContainer;

/**
 * UnifyCode helper task registration.
 */
public final class UnifycodeTasks {
    /**
     * Gradle task container.
     */
    private final TaskContainer tasks;

    /**
     * New helper task registration.
     *
     * @param tasks gradle task container.
     */
    public UnifycodeTasks(final TaskContainer tasks) {
        this.tasks = tasks;
    }

    /**
     * Configure helper tasks.
     *
     * @todo #2:30min Make unifycodeFormat and unifycodeCheck fields instead of hard coded values.
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
        return this.taskNamed("spotlessApply");
    }

    private Iterable<? extends Task> spotlessCheckTasks() {
        return this.taskNamed("spotlessCheck");
    }

    private Iterable<? extends Task> checkstyleTasks() {
        return this.tasks.withType(Checkstyle.class);
    }

    private Iterable<? extends Task> pmdTasks() {
        return this.tasks.withType(Pmd.class);
    }

    private Iterable<? extends Task> taskNamed(final String name) {
        final Task task = this.tasks.findByName(name);
        if (task == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(task);
    }
}
