package io.github.c71n93.unifycode.gradle.tool;

import java.io.File;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;

/**
 * Facade over Gradle project capabilities available to tool configurators.
 */
final class ToolProject {
    /**
     * Current Gradle project.
     */
    private final Project project;

    /**
     * New project facade.
     *
     * @param project current project.
     */
    public ToolProject(final Project project) {
        this.project = project;
    }

    /**
     * Applies a plugin.
     *
     * @param plugin plugin id.
     */
    public void applyPlugin(final String plugin) {
        this.project.getPluginManager().apply(plugin);
    }

    /**
     * Configures an extension.
     *
     * @param type extension type.
     * @param action configuration action.
     * @param <T> extension type.
     */
    public <T> void configureExtension(final Class<T> type, final Action<? super T> action) {
        this.project.getExtensions().configure(type, action);
    }

    /**
     * File collection for the given file.
     *
     * @param file file.
     * @return file collection.
     */
    public FileCollection files(final File file) {
        return this.project.files(file);
    }
}
