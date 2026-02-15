package io.unifycode.java;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class UnifycodeJavaPluginFunctionalTest {
    @TempDir
    Path testProjectDir;

    @Test
    void checkTaskDependsOnAllQualityChecks() throws IOException {
        writeConsumerProject();
        final BuildResult result = GradleRunner.create()
                .withProjectDir(testProjectDir.toFile())
                .withArguments("check", "--dry-run")
                .withPluginClasspath()
                .build();
        assertTrue(
                result.getOutput().contains(":spotlessCheck SKIPPED"),
                () -> "Expected spotlessCheck to be wired into check.\nOutput:\n" + result.getOutput()
        );
        assertTrue(
                result.getOutput().contains(":pmdMain SKIPPED"),
                () -> "Expected pmdMain to be wired into check.\nOutput:\n" + result.getOutput()
        );
        assertTrue(
                result.getOutput().contains(":checkstyleMain SKIPPED"),
                () -> "Expected checkstyleMain to be wired into check.\nOutput:\n" + result.getOutput()
        );
    }

    @Test
    void formatTaskDependsOnSpotlessApply() throws IOException {
        writeConsumerProject();
        final BuildResult result = GradleRunner.create()
                .withProjectDir(testProjectDir.toFile())
                .withArguments("format", "--dry-run")
                .withPluginClasspath()
                .build();
        assertTrue(
                result.getOutput().contains(":spotlessApply SKIPPED"),
                () -> "Expected format task to depend on spotlessApply.\nOutput:\n" + result.getOutput()
        );
    }

    @Test
    void unifycodeCheckDependsOnStaticAnalysisTasksOnly() throws IOException {
        writeConsumerProject();
        final BuildResult result = GradleRunner.create()
                .withProjectDir(testProjectDir.toFile())
                .withArguments("unifycodeCheck", "--dry-run")
                .withPluginClasspath()
                .build();
        assertTrue(
                result.getOutput().contains(":spotlessCheck SKIPPED"),
                () -> "Expected unifycodeCheck to include spotlessCheck.\nOutput:\n" + result.getOutput()
        );
        assertTrue(
                result.getOutput().contains(":pmdMain SKIPPED"),
                () -> "Expected unifycodeCheck to include pmdMain.\nOutput:\n" + result.getOutput()
        );
        assertTrue(
                result.getOutput().contains(":checkstyleMain SKIPPED"),
                () -> "Expected unifycodeCheck to include checkstyleMain.\nOutput:\n" + result.getOutput()
        );
        assertFalse(
                result.getOutput().contains(":test SKIPPED"),
                () -> "Expected unifycodeCheck not to include test task.\nOutput:\n" + result.getOutput()
        );
    }

    private void writeConsumerProject() throws IOException {
        writeFile("settings.gradle", "rootProject.name = 'plugin-functional-test'\n");
        writeFile(
                "build.gradle",
                "plugins {\n"
                        + "    id 'java'\n"
                        + "    id 'io.unifycode.java'\n"
                        + "}\n"
                        + "\n"
                        + "repositories {\n"
                        + "    mavenCentral()\n"
                        + "}\n"
        );
        writeFile(
                "src/main/java/demo/App.java",
                "package demo;\n"
                        + "\n"
                        + "public class App {\n"
                        + "    public static void main(String[] args) {\n"
                        + "        System.out.println(\"ok\");\n"
                        + "    }\n"
                        + "}\n"
        );
    }

    private void writeFile(final String relativePath, final String content) throws IOException {
        final Path filePath = testProjectDir.resolve(relativePath);
        final Path parentPath = filePath.getParent();
        if (parentPath != null) {
            Files.createDirectories(parentPath);
        }
        Files.writeString(filePath, content);
    }
}
