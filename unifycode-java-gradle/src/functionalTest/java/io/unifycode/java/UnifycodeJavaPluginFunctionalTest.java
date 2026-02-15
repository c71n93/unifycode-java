package io.unifycode.java;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class UnifycodeJavaPluginFunctionalTest {
    @TempDir
    Path testProjectDir;

    @Test
    void checkTaskDependsOnAllQualityChecks() throws IOException {
        this.writeConsumerProject();
        final BuildResult result = GradleRunner.create()
                .withProjectDir(this.testProjectDir.toFile())
                .withArguments("check", "--dry-run")
                .withPluginClasspath()
                .build();
        Assertions.assertTrue(
                result.getOutput().contains(":spotlessCheck SKIPPED"),
                () -> "Expected spotlessCheck to be wired into check.\nOutput:\n" + result.getOutput()
        );
        Assertions.assertTrue(
                result.getOutput().contains(":pmdMain SKIPPED"),
                () -> "Expected pmdMain to be wired into check.\nOutput:\n" + result.getOutput()
        );
        Assertions.assertTrue(
                result.getOutput().contains(":checkstyleMain SKIPPED"),
                () -> "Expected checkstyleMain to be wired into check.\nOutput:\n" + result.getOutput()
        );
    }

    @Test
    void formatTaskDependsOnSpotlessApply() throws IOException {
        this.writeConsumerProject();
        final BuildResult result = GradleRunner.create()
                .withProjectDir(this.testProjectDir.toFile())
                .withArguments("format", "--dry-run")
                .withPluginClasspath()
                .build();
        Assertions.assertTrue(
                result.getOutput().contains(":spotlessApply SKIPPED"),
                () -> "Expected format task to depend on spotlessApply.\nOutput:\n" + result.getOutput()
        );
    }

    @Test
    void unifycodeCheckDependsOnStaticAnalysisTasksOnly() throws IOException {
        this.writeConsumerProject();
        final BuildResult result = GradleRunner.create()
                .withProjectDir(this.testProjectDir.toFile())
                .withArguments("unifycodeCheck", "--dry-run")
                .withPluginClasspath()
                .build();
        Assertions.assertTrue(
                result.getOutput().contains(":spotlessCheck SKIPPED"),
                () -> "Expected unifycodeCheck to include spotlessCheck.\nOutput:\n" + result.getOutput()
        );
        Assertions.assertTrue(
                result.getOutput().contains(":pmdMain SKIPPED"),
                () -> "Expected unifycodeCheck to include pmdMain.\nOutput:\n" + result.getOutput()
        );
        Assertions.assertTrue(
                result.getOutput().contains(":checkstyleMain SKIPPED"),
                () -> "Expected unifycodeCheck to include checkstyleMain.\nOutput:\n" + result.getOutput()
        );
        Assertions.assertFalse(
                result.getOutput().contains(":test SKIPPED"),
                () -> "Expected unifycodeCheck not to include test task.\nOutput:\n" + result.getOutput()
        );
    }

    private void writeConsumerProject() throws IOException {
        this.writeFile("settings.gradle", "rootProject.name = 'plugin-functional-test'\n");
        this.writeFile(
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
        this.writeFile(
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
        final Path filePath = this.testProjectDir.resolve(relativePath);
        final Path parentPath = filePath.getParent();
        if (parentPath != null) {
            Files.createDirectories(parentPath);
        }
        Files.writeString(filePath, content);
    }
}
