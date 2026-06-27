package io.github.c71n93.unifycode.gradle.tool;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for the UnifyCode resource copier.
 */
final class UnifycodeResourcesTest {
    /**
     * Bundled tool resource paths.
     */
    private static final String[] RESOURCES = {
        "io/github/c71n93/unifycode/checkstyle/checkstyle.xml",
        "io/github/c71n93/unifycode/checkstyle/checkstyle-suppressions.xml",
        "io/github/c71n93/unifycode/pmd/pmd.xml",
        "io/github/c71n93/unifycode/spotless/eclipse-java-formatter.xml",
    };

    /**
     * Temporary resource copy target.
     */
    @TempDir
    private Path targetDir;

    @Test
    void copiesBundledToolResources() {
        final UnifycodeResources resources = new UnifycodeResources(this.targetDir.toFile());
        for (final String resource : UnifycodeResourcesTest.RESOURCES) {
            final File copied = resources.copy(resource);
            Assertions.assertTrue(copied.exists(), () -> "Expected copied resource to exist: " + resource);
            Assertions.assertTrue(copied.length() > 0L, () -> "Expected copied resource to be non-empty: " + resource);
            Assertions.assertTrue(Files.isRegularFile(copied.toPath()), () -> "Expected regular file: " + resource);
        }
    }
}
