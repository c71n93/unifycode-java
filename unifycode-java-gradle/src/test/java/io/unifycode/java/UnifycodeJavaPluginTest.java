package io.unifycode.java;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UnifycodeJavaPluginTest {
    private Project project;

    @BeforeAll
    void setUpProject() {
        this.project = ProjectBuilder.builder().build();
        this.project.getPluginManager().apply("java");
        new UnifycodeJavaPlugin().apply(this.project);
    }

    @Test
    void applyRegistersDependentPlugins() {
        Assertions.assertNotNull(
                this.project.getPlugins().findPlugin("checkstyle"), "Expected checkstyle plugin to be applied."
        );
        Assertions.assertNotNull(this.project.getPlugins().findPlugin("pmd"), "Expected pmd plugin to be applied.");
        Assertions.assertNotNull(
                this.project.getPlugins().findPlugin("com.diffplug.spotless"),
                "Expected spotless plugin to be applied."
        );
    }

    @Test
    void applyRegistersExpectedTasks() {
        final Task formatTask = this.project.getTasks().getByName("format");
        final Set<String> formatDependencies = formatTask.getTaskDependencies()
                .getDependencies(formatTask)
                .stream()
                .map(Task::getName)
                .collect(Collectors.toSet());
        Assertions.assertTrue(
                formatDependencies.contains("spotlessApply"),
                "Expected format task to depend on spotlessApply."
        );

        Assertions.assertNotNull(this.project.getTasks().findByName("check"), "Expected check task to exist.");
        Assertions.assertNotNull(
                this.project.getTasks().findByName("spotlessCheck"), "Expected spotlessCheck task to exist."
        );
        Assertions.assertNotNull(
                this.project.getTasks().findByName("unifycodeCheck"), "Expected unifycodeCheck task to exist."
        );
    }

    @Test
    void unifycodeCheckDependsOnStaticAnalysisTasksOnly() {
        final Task unifycodeCheckTask = this.project.getTasks().getByName("unifycodeCheck");
        final Set<String> dependencies = unifycodeCheckTask.getTaskDependencies()
                .getDependencies(unifycodeCheckTask)
                .stream()
                .map(Task::getName)
                .collect(Collectors.toSet());

        Assertions.assertTrue(
                dependencies.contains("spotlessCheck"), "Expected unifycodeCheck to depend on spotlessCheck."
        );
        Assertions.assertTrue(dependencies.contains("pmdMain"), "Expected unifycodeCheck to depend on pmdMain.");
        Assertions.assertTrue(
                dependencies.contains("checkstyleMain"), "Expected unifycodeCheck to depend on checkstyleMain."
        );
        Assertions.assertFalse(dependencies.contains("test"), "Expected unifycodeCheck not to depend on test.");
    }

    @Test
    void applyCopiesExpectedConfigFiles() {
        final Path unifycodeDir = this.project.getLayout().getBuildDirectory().getAsFile().get().toPath()
                .resolve("unifycode");
        Assertions.assertTrue(
                Files.exists(unifycodeDir.resolve("checkstyle.xml")), "Expected checkstyle.xml to be copied."
        );
        Assertions.assertTrue(Files.exists(unifycodeDir.resolve("pmd.xml")), "Expected pmd.xml to be copied.");
        Assertions.assertTrue(
                Files.exists(unifycodeDir.resolve("eclipse-java-formatter.xml")),
                "Expected eclipse-java-formatter.xml to be copied."
        );
    }
}
