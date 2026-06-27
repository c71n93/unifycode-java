package org.unifycode.gradle;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;

/**
 * Gradle TestKit fixture project.
 */
@SuppressWarnings("PMD.TooManyMethods")
final class GradleFixtureProject {
    /**
     * Consumer project root directory.
     */
    private final Path projectDir;

    /**
     * New fixture project.
     *
     * @param projectDir Consumer project root directory.
     */
    /* default */ GradleFixtureProject(final Path projectDir) {
        this.projectDir = projectDir;
    }

    /**
     * Consumer build file fixture.
     *
     * @param name Build fixture name.
     * @throws IOException On fixture write failure.
     */
    /* default */ void writeBuild(final String name) throws IOException {
        this.write("settings.gradle", "rootProject.name = 'plugin-functional-test'\n");
        this.writeFixture("build-scripts/" + name + ".gradle", "build.gradle");
        this.writeDefaultMainSource();
    }

    /**
     * Default main source fixture.
     *
     * @throws IOException On fixture write failure.
     */
    /* default */ void writeDefaultMainSource() throws IOException {
        this.writeMainSource("valid-app");
        this.write(
            "src/main/java/demo/package-info.java",
            "/**\n"
                + " * Demo package.\n"
                + " */\n"
                + "package demo;\n"
        );
    }

    /**
     * Main source fixture.
     *
     * @param name Source fixture name.
     * @throws IOException On fixture write failure.
     */
    /* default */ void writeMainSource(final String name) throws IOException {
        this.writeFixture("main/" + name + ".java", "src/main/java/demo/App.java");
    }

    /**
     * Test source fixture.
     *
     * @param name Source fixture name.
     * @throws IOException On fixture write failure.
     */
    /* default */ void writeTestSource(final String name) throws IOException {
        this.writeFixture("test/" + name + ".java", "src/test/java/demo/AppTest.java");
    }

    /**
     * Test package information fixture.
     *
     * @throws IOException On fixture write failure.
     */
    /* default */ void writeTestPackageInfo() throws IOException {
        this.writeFixture("test/package-info.java", "src/test/java/demo/package-info.java");
    }

    /**
     * Raw fixture copy operation.
     *
     * @param fixturePath Fixture resource path.
     * @param projectPath Consumer project path.
     * @throws IOException On fixture write failure.
     */
    /* default */ void writeFixture(final String fixturePath, final String projectPath) throws IOException {
        this.write(projectPath, GradleFixtureProject.readFixture("/fixtures/" + fixturePath));
    }

    /**
     * Successful Gradle build result.
     *
     * @param arguments Build arguments.
     * @return Build result.
     */
    /* default */ BuildResult succeeds(final String... arguments) {
        return this.runner(arguments).build();
    }

    /**
     * Failed Gradle build result.
     *
     * @param arguments Build arguments.
     * @return Build result.
     */
    /* default */ BuildResult fails(final String... arguments) {
        return this.runner(arguments).buildAndFail();
    }

    /**
     * Checkstyle main XML report.
     *
     * @return Report content.
     * @throws IOException On report read failure.
     */
    /* default */ String checkstyleMainReport() throws IOException {
        return this.readProjectFile("build/reports/checkstyle/main.xml");
    }

    /**
     * Checkstyle test XML report.
     *
     * @return Report content.
     * @throws IOException On report read failure.
     */
    /* default */ String checkstyleTestReport() throws IOException {
        return this.readProjectFile("build/reports/checkstyle/test.xml");
    }

    /**
     * PMD main XML report.
     *
     * @return Report content.
     * @throws IOException On report read failure.
     */
    /* default */ String pmdMainReport() throws IOException {
        return this.readProjectFile("build/reports/pmd/main.xml");
    }

    private GradleRunner runner(final String... arguments) {
        final List<String> actual = new ArrayList<>(Arrays.asList(arguments));
        actual.add("--stacktrace");
        return GradleRunner.create()
            .withProjectDir(this.projectDir.toFile())
            .withArguments(actual)
            .withPluginClasspath();
    }

    private String readProjectFile(final String projectPath) throws IOException {
        return Files.readString(this.projectDir.resolve(projectPath));
    }

    private void write(final String projectPath, final String content) throws IOException {
        final Path file = this.projectDir.resolve(projectPath);
        final Path parent = file.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(file, content);
    }

    private static String readFixture(final String resourcePath) throws IOException {
        try (InputStream stream = GradleFixtureProject.class.getResourceAsStream(resourcePath)) {
            if (stream == null) {
                throw new IOException("Missing fixture resource: " + resourcePath);
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8)
                .replace("\r\n", "\n")
                .replace('\r', '\n');
        }
    }
}
