package io.unifycode.java;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UnifycodeJavaPluginTest {
    private Project project;

    @BeforeAll
    void setUpProject() {
        project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("java");
        new UnifycodeJavaPlugin().apply(project);
    }

    @Test
    void applyRegistersDependentPlugins() {
        assertNotNull(project.getPlugins().findPlugin("checkstyle"));
        assertNotNull(project.getPlugins().findPlugin("pmd"));
        assertNotNull(project.getPlugins().findPlugin("com.diffplug.spotless"));
    }

    @Test
    void applyRegistersExpectedTasks() {
        final Task formatTask = project.getTasks().getByName("format");
        final Set<String> formatDependencies = formatTask.getTaskDependencies()
                .getDependencies(formatTask)
                .stream()
                .map(Task::getName)
                .collect(Collectors.toSet());
        assertTrue(formatDependencies.contains("spotlessApply"));

        assertNotNull(project.getTasks().findByName("check"));
        assertNotNull(project.getTasks().findByName("spotlessCheck"));
        assertNotNull(project.getTasks().findByName("unifycodeCheck"));
    }

    @Test
    void unifycodeCheckDependsOnStaticAnalysisTasksOnly() {
        final Task unifycodeCheckTask = project.getTasks().getByName("unifycodeCheck");
        final Set<String> dependencies = unifycodeCheckTask.getTaskDependencies()
                .getDependencies(unifycodeCheckTask)
                .stream()
                .map(Task::getName)
                .collect(Collectors.toSet());

        assertTrue(dependencies.contains("spotlessCheck"));
        assertTrue(dependencies.contains("pmdMain"));
        assertTrue(dependencies.contains("checkstyleMain"));
        assertFalse(dependencies.contains("test"));
    }

    @Test
    void applyCopiesExpectedConfigFiles() {
        final Path unifycodeDir = project.getLayout().getBuildDirectory().getAsFile().get().toPath().resolve("unifycode");
        assertTrue(Files.exists(unifycodeDir.resolve("checkstyle.xml")));
        assertTrue(Files.exists(unifycodeDir.resolve("pmd.xml")));
        assertTrue(Files.exists(unifycodeDir.resolve("eclipse-java-formatter.xml")));
    }
}
