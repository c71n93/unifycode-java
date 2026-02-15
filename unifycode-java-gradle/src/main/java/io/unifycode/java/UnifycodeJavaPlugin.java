package io.unifycode.java;

import com.diffplug.gradle.spotless.SpotlessExtension;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.quality.Checkstyle;
import org.gradle.api.plugins.quality.CheckstyleExtension;
import org.gradle.api.plugins.quality.Pmd;
import org.gradle.api.plugins.quality.PmdExtension;

public class UnifycodeJavaPlugin implements Plugin<Project> {
    private static final String CHECKSTYLE_CONFIG_RESOURCE = "unifycode/checkstyle/checkstyle.xml";
    private static final String PMD_CONFIG_RESOURCE = "unifycode/pmd/pmd.xml";
    private static final String SPOTLESS_FORMATTER_RESOURCE = "unifycode/spotless/eclipse-java-formatter.xml";

    // TODO: Decompose this method.
    @Override
    public void apply(final Project project) {
        project.getPluginManager().apply("checkstyle");
        project.getPluginManager().apply("pmd");
        project.getPluginManager().apply("com.diffplug.spotless");

        final File checkstyleConfig = copyResourceToBuildDir(
                project,
                CHECKSTYLE_CONFIG_RESOURCE,
                "checkstyle.xml"
        );
        final File pmdConfig = copyResourceToBuildDir(
                project,
                PMD_CONFIG_RESOURCE,
                "pmd.xml"
        );
        final File spotlessFormatterConfig = copyResourceToBuildDir(
                project,
                SPOTLESS_FORMATTER_RESOURCE,
                "eclipse-java-formatter.xml"
        );

        project.getExtensions()
                .configure(CheckstyleExtension.class, extension -> extension.setConfigFile(checkstyleConfig));
        project.getExtensions().configure(PmdExtension.class, extension -> {
            extension.setToolVersion("7.0.0");
            extension.setConsoleOutput(true);
            extension.setRuleSets(Collections.emptyList());
            extension.setRuleSetFiles(project.files(pmdConfig));
        });
        project.getExtensions().configure(SpotlessExtension.class, extension -> extension.java(java -> {
            java.eclipse().configFile(spotlessFormatterConfig);
        }));

        if (project.getTasks().findByName("format") == null) {
            project.getTasks().register("format", task -> {
                task.setGroup("formatting");
                task.setDescription("Runs source formatting via Spotless.");
                task.dependsOn("spotlessApply");
            });
        }

        if (project.getTasks().findByName("unifycodeCheck") == null) {
            project.getTasks().register("unifycodeCheck", task -> {
                task.setGroup("verification");
                task.setDescription("Runs static analysis checks without launching tests.");
                task.dependsOn("spotlessCheck");
                task.dependsOn(project.getTasks().withType(Checkstyle.class));
                task.dependsOn(project.getTasks().withType(Pmd.class));
            });
        }
    }

    private static File copyResourceToBuildDir(
            final Project project,
            final String resourcePath,
            final String targetFilename) {
        final File targetDir = new File(
                project.getLayout().getBuildDirectory().getAsFile().get(),
                "unifycode"
        );
        if (!targetDir.exists() && !targetDir.mkdirs()) {
            throw new GradleException("Could not create directory: " + targetDir);
        }

        final File targetFile = new File(targetDir, targetFilename);
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new GradleException("Could not load resource: " + resourcePath);
            }
            Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return targetFile;
        } catch (IOException exception) {
            throw new UncheckedIOException("Could not copy resource: " + resourcePath, exception);
        }
    }
}
