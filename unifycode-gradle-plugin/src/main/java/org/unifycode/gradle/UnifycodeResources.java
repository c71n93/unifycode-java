package org.unifycode.gradle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.gradle.api.GradleException;
import org.gradle.api.Project;

final class UnifycodeResources {
    /**
     * Target directory for copied resources.
     */
    private final File targetDir;

    UnifycodeResources(final File targetDir) {
        this.targetDir = targetDir;
    }

    UnifycodeResources(final Project project) {
        this(createTargetDir(project));
    }

    File copy(final String resourcePath, final String targetFilename) {
        final File targetFile = new File(this.targetDir, targetFilename);
        try (InputStream input = UnifycodeResources.class.getResourceAsStream("/" + resourcePath)) {
            if (input == null) {
                throw new GradleException("Could not load resource: " + resourcePath);
            }
            Files.copy(input, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return targetFile;
        } catch (final IOException exception) {
            throw new UncheckedIOException("Could not copy resource: " + resourcePath, exception);
        }
    }

    File copy(final String resourcePath) {
        // Resolve the target name from the last path segment for predictable output files.
        final int separator = resourcePath.lastIndexOf('/');
        final String targetFilename;
        if (separator < 0) {
            targetFilename = resourcePath;
        } else {
            targetFilename = resourcePath.substring(separator + 1);
        }
        return this.copy(resourcePath, targetFilename);
    }

    private static File createTargetDir(final Project project) {
        final File targetDir = new File(
            project.getLayout().getBuildDirectory().getAsFile().get(),
            "unifycode"
        );
        if (!targetDir.exists() && !targetDir.mkdirs()) {
            throw new GradleException("Could not create directory: " + targetDir);
        }
        return targetDir;
    }
}
