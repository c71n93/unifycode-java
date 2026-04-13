package org.unifycode.gradle;

import java.util.Collections;
import java.util.concurrent.Callable;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.quality.Checkstyle;
import org.gradle.api.plugins.quality.Pmd;

final class UnifycodeTasks {
    private final Project project;
    private final UnifycodeExtension extension;

    UnifycodeTasks(final Project project, final UnifycodeExtension extension) {
        this.project = project;
        this.extension = extension;
    }

    void configure() {
        // TODO: refactor this AI slop later.
        this.configureTask("format", task -> {
            task.setGroup("formatting");
            task.setDescription("Runs source formatting via Spotless.");
            task.dependsOn((Callable<Object>) this::spotlessApplyTask);
        });
        this.configureTask("unifycodeCheck", task -> {
            task.setGroup("verification");
            task.setDescription("Runs static analysis checks without launching tests.");
            task.dependsOn((Callable<Object>) this::spotlessCheckTask);
            task.dependsOn((Callable<Object>) this::checkstyleTasks);
            task.dependsOn((Callable<Object>) this::pmdTasks);
        });
    }

    private void configureTask(final String name, final Action<Task> action) {
        final Task task = this.project.getTasks().findByName(name);
        if (task == null) {
            this.project.getTasks().register(name, action);
            return;
        }
        action.execute(task);
    }

    private Object spotlessApplyTask() {
        return this.taskNamedWhenEnabled("spotlessApply", this.extension.getSpotlessEnabled().get());
    }

    private Object spotlessCheckTask() {
        return this.taskNamedWhenEnabled("spotlessCheck", this.extension.getSpotlessEnabled().get());
    }

    private Object checkstyleTasks() {
        if (!this.extension.getCheckstyleEnabled().get()) {
            return Collections.emptyList();
        }
        return this.project.getTasks().withType(Checkstyle.class);
    }

    private Object pmdTasks() {
        if (!this.extension.getPmdEnabled().get()) {
            return Collections.emptyList();
        }
        return this.project.getTasks().withType(Pmd.class);
    }

    private Object taskNamedWhenEnabled(final String name, final boolean enabled) {
        if (!enabled) {
            return Collections.emptyList();
        }
        final Task task = this.project.getTasks().findByName(name);
        if (task == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(task);
    }
}
