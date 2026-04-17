package org.unifycode.spotless;

import java.io.IOException;
import java.util.Locale;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class SpotlessFormatterTest {

    @Test
    void formatsDocumentation() throws Exception {
        Assertions.assertDoesNotThrow(() -> this.fixture("Documentation"), "Documentation fixture should pass");
    }

    @Test
    void formatsMethodDeclarations() throws Exception {
        Assertions.assertDoesNotThrow(
                () -> this.fixture("MethodDeclarations"),
                "Method declarations fixture should pass"
        );
    }

    @Test
    void formatsMethodCalls() throws Exception {
        Assertions.assertDoesNotThrow(() -> this.fixture("MethodCalls"), "Method calls fixture should pass");
    }

    @Test
    void formatsAnnotations() throws Exception {
        Assertions.assertDoesNotThrow(() -> this.fixture("Annotations"), "Annotations fixture should pass");
    }

    @Test
    void formatsExpressions() throws Exception {
        Assertions.assertDoesNotThrow(() -> this.fixture("Expressions"), "Expressions fixture should pass");
    }

    @Test
    void formatsRecordCtorParameters() throws Exception {
        Assertions.assertDoesNotThrow(
                () -> this.fixture("RecordCtorParameters"),
                "Record ctor parameters fixture should pass"
        );
    }

    private void fixture(final String name) throws Exception {
        final String input = this.resource("fixtures/" + name + "Input.java");
        final String expected = this.expectedResource("fixtures/" + name + "Expected.java");
        final Path project = Files.createTempDirectory("spotless-" + name.toLowerCase(Locale.ROOT));
        this.project(project, input);
        final String output = this.afterSpotlessApply(project);
        Assertions.assertFalse(output.equals(input), "Fixture must start badly formatted");
        Assertions.assertEquals(expected, output, "Formatted output should match expected fixture");
        this.project(project, expected);
        this.gradle(project, "spotlessCheck");
        Assertions.assertEquals(
                expected,
                this.afterSpotlessApply(project),
                "Expected fixture should stay stable after Spotless run"
        );
    }

    private void project(final Path project, final String source) throws IOException {
        Files.writeString(
                project.resolve("settings.gradle"),
                "rootProject.name = 'spotless-fixture'\n",
                StandardCharsets.UTF_8
        );
        Files.writeString(project.resolve("build.gradle"), this.buildScript(), StandardCharsets.UTF_8);
        Files.writeString(
                project.resolve("eclipse-java-formatter.xml"),
                this.resource("/unifycode/spotless/eclipse-java-formatter.xml"),
                StandardCharsets.UTF_8
        );
        Files.createDirectories(project.resolve("src/main/java/fixture"));
        Files.writeString(
                project.resolve("src/main/java/fixture/Fixture.java"),
                source,
                StandardCharsets.UTF_8
        );
    }

    private String buildScript() {
        // TODO: Optimize this build script. Now it resolves Spotless dependency via
        // network every new build. Theoretically we can use Spotless dependency that we
        // already have in our project.
        return String.join(
                "\n",
                "plugins {",
                "    id 'java'",
                "    id 'com.diffplug.spotless' version '8.4.0'",
                "}",
                "",
                "repositories {",
                "    mavenCentral()",
                "}",
                "",
                "spotless {",
                "    java {",
                "        target 'src/main/java/**/*.java'",
                "        eclipse().configFile('eclipse-java-formatter.xml')",
                "    }",
                "}",
                ""
        );
    }

    private String afterSpotlessApply(final Path project) throws IOException {
        this.gradle(project, "spotlessApply");
        return Files.readString(
                project.resolve("src/main/java/fixture/Fixture.java"),
                StandardCharsets.UTF_8
        ).replace("\r\n", "\n");
    }

    private void gradle(final Path project, final String task) {
        GradleRunner.create()
                .withProjectDir(project.toFile())
                .withArguments(task, "--stacktrace")
                .forwardOutput()
                .build();
    }

    private String resource(final String path) throws IOException {
        final String normalized = path.startsWith("/") ? path.substring(1) : path;
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (final var stream = loader.getResourceAsStream(normalized)) {
            if (stream == null) {
                throw new IOException("Missing resource: " + path);
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8).replace("\r\n", "\n");
        }
    }

    private String expectedResource(final String path) throws IOException {
        return this.resource(path).replaceAll("(?m)^\\s*// TODO.*\\n?", "");
    }
}
