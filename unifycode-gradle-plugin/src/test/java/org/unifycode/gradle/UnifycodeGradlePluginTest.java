package org.unifycode.gradle;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class UnifycodeGradlePluginTest {
    /**
     * Formatting helper task name.
     */
    private static final String FORMAT = "unifycodeFormat";

    /**
     * Verification helper task name.
     */
    private static final String UNIFYCODE_CHECK = "unifycodeCheck";

    @Test
    void applyCreatesExtensionWithEnabledDefaults() {
        final Project project = ProjectBuilder.builder().build();
        new UnifycodeGradlePlugin().apply(project);
        final UnifycodeExtension extension = project.getExtensions().getByType(UnifycodeExtension.class);
        Assertions.assertTrue(extension.getCheckstyleEnabled().get(), "Expected Checkstyle to be enabled by default.");
        Assertions.assertTrue(extension.getPmdEnabled().get(), "Expected PMD to be enabled by default.");
        Assertions.assertTrue(extension.getSpotlessEnabled().get(), "Expected Spotless to be enabled by default.");
    }

    @Test
    void applyRegistersHelperTasksForNonJavaProject() {
        final Project project = ProjectBuilder.builder().build();
        new UnifycodeGradlePlugin().apply(project);
        Assertions
            .assertNotNull(
                this.task(project, UnifycodeGradlePluginTest.FORMAT),
                "Expected unifycodeFormat task to exist."
            );
        Assertions.assertNotNull(
            this.task(project, UnifycodeGradlePluginTest.UNIFYCODE_CHECK),
            "Expected unifycodeCheck task to exist."
        );
        Assertions.assertNull(
            project.getPlugins().findPlugin("checkstyle"),
            "Expected Checkstyle plugin not to be applied."
        );
        Assertions.assertNull(project.getPlugins().findPlugin("pmd"), "Expected PMD plugin not to be applied.");
        Assertions.assertNull(
            project.getPlugins().findPlugin("com.diffplug.spotless"),
            "Expected Spotless plugin not to be applied."
        );
    }

    @Test
    void applyFailsWhenProjectAlreadyDefinesHelperTask() {
        final Project project = ProjectBuilder.builder().build();
        project.getTasks().register(UnifycodeGradlePluginTest.FORMAT);
        final InvalidUserDataException exception = Assertions.assertThrows(
            InvalidUserDataException.class,
            () -> new UnifycodeGradlePlugin().apply(project)
        );
        Assertions.assertTrue(
            exception.getMessage().contains(UnifycodeGradlePluginTest.FORMAT),
            "Expected duplicate task error to mention unifycodeFormat."
        );
    }

    @Test
    void applyBeforeJavaConfiguresToolsAfterEvaluation() {
        final Project project = ProjectBuilder.builder().build();
        new UnifycodeGradlePlugin().apply(project);
        project.getPluginManager().apply("java");
        this.evaluate(project);
        Assertions.assertNotNull(
            project.getPlugins().findPlugin("checkstyle"),
            "Expected Checkstyle plugin to be applied."
        );
        Assertions.assertNotNull(project.getPlugins().findPlugin("pmd"), "Expected PMD plugin to be applied.");
        Assertions.assertNotNull(
            project.getPlugins().findPlugin("com.diffplug.spotless"),
            "Expected Spotless plugin to be applied."
        );
        Assertions.assertTrue(
            this.dependencies(this.task(project, UnifycodeGradlePluginTest.FORMAT)).contains("spotlessApply"),
            "Expected unifycodeFormat task to depend on spotlessApply."
        );
        Assertions.assertTrue(
            this.dependencies(this.task(project, UnifycodeGradlePluginTest.UNIFYCODE_CHECK))
                .contains("spotlessCheck"),
            "Expected unifycodeCheck to depend on spotlessCheck."
        );
        Assertions.assertTrue(
            this.dependencies(this.task(project, UnifycodeGradlePluginTest.UNIFYCODE_CHECK)).contains("pmdMain"),
            "Expected unifycodeCheck to depend on pmdMain."
        );
        Assertions.assertTrue(
            this.dependencies(this.task(project, UnifycodeGradlePluginTest.UNIFYCODE_CHECK))
                .contains("checkstyleMain"),
            "Expected unifycodeCheck to depend on checkstyleMain."
        );
        Assertions.assertTrue(
            this.dependencies(this.task(project, "check")).contains(UnifycodeGradlePluginTest.UNIFYCODE_CHECK),
            "Expected check to depend on unifycodeCheck."
        );
    }

    @Test
    void applyCopiesExpectedConfigFilesForJavaProject() {
        final Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("java");
        new UnifycodeGradlePlugin().apply(project);
        this.evaluate(project);
        final Path unifycode = project.getLayout().getBuildDirectory().getAsFile().get().toPath().resolve("unifycode");
        Assertions.assertTrue(
            Files.exists(unifycode.resolve("checkstyle.xml")),
            "Expected checkstyle.xml to be copied."
        );
        Assertions.assertTrue(
            Files.exists(unifycode.resolve("checkstyle-suppressions.xml")),
            "Expected checkstyle-suppressions.xml to be copied."
        );
        Assertions.assertTrue(Files.exists(unifycode.resolve("pmd.xml")), "Expected pmd.xml to be copied.");
        Assertions.assertTrue(
            Files.exists(unifycode.resolve("eclipse-java-formatter.xml")),
            "Expected eclipse-java-formatter.xml to be copied."
        );
    }

    @Test
    void disabledToolsLeaveHelperTasksAsNoOp() {
        final Project project = ProjectBuilder.builder().build();
        new UnifycodeGradlePlugin().apply(project);
        final UnifycodeExtension extension = project.getExtensions().getByType(UnifycodeExtension.class);
        extension.getCheckstyleEnabled().set(false);
        extension.getPmdEnabled().set(false);
        extension.getSpotlessEnabled().set(false);
        project.getPluginManager().apply("java");
        this.evaluate(project);
        Assertions.assertNull(
            project.getPlugins().findPlugin("checkstyle"),
            "Expected Checkstyle plugin not to be applied."
        );
        Assertions.assertNull(project.getPlugins().findPlugin("pmd"), "Expected PMD plugin not to be applied.");
        Assertions.assertNull(
            project.getPlugins().findPlugin("com.diffplug.spotless"),
            "Expected Spotless plugin not to be applied."
        );
        Assertions.assertTrue(
            this.dependencies(this.task(project, UnifycodeGradlePluginTest.FORMAT)).isEmpty(),
            "Expected unifycodeFormat task to have no dependencies."
        );
        Assertions.assertTrue(
            this.dependencies(this.task(project, UnifycodeGradlePluginTest.UNIFYCODE_CHECK)).isEmpty(),
            "Expected unifycodeCheck task to have no dependencies."
        );
        final Path unifycode = project.getLayout().getBuildDirectory().getAsFile().get().toPath().resolve("unifycode");
        Assertions.assertFalse(Files.exists(unifycode), "Expected no unifycode config files to be copied.");
    }

    private Task task(final Project project, final String name) {
        return project.getTasks().getByName(name);
    }

    private Set<String> dependencies(final Task task) {
        return task.getTaskDependencies()
            .getDependencies(task)
            .stream()
            .map(Task::getName)
            .collect(Collectors.toSet());
    }

    private void evaluate(final Project project) {
        ((ProjectInternal) project).evaluate();
    }
}
