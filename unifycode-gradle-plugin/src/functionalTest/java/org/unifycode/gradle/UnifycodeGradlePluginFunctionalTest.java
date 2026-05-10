package org.unifycode.gradle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

// @checkstyle JavadocVariable (25 lines)
final class UnifycodeGradlePluginFunctionalTest {
    private static final String JAVA_AND_UNIFYCODE = "    id 'java'\n    id 'org.unifycode'\n";

    private static final String UNIFYCODE_AND_JAVA = "    id 'org.unifycode'\n    id 'java'\n";

    private static final String UNIFYCODE_ONLY = "    id 'org.unifycode'\n";

    private static final String DRY_RUN = "--dry-run";

    private static final String FORMAT = "unifycodeFormat";

    private static final String UNIFYCODE_CHECK = "unifycodeCheck";

    private static final String UNIFYCODE_CHECK_SKIPPED = ":unifycodeCheck SKIPPED";

    private static final String SPOTLESS_APPLY = ":spotlessApply SKIPPED";

    private static final String SPOTLESS_CHECK = ":spotlessCheck SKIPPED";

    private static final String PMD_MAIN = ":pmdMain SKIPPED";

    private static final String CHECKSTYLE_MAIN = ":checkstyleMain SKIPPED";

    private static final String BLOCK_END = "}\n";

    /**
     * Temporary consumer project directory.
     */
    @TempDir
    private Path testProjectDir;

    @Test
    void checkTaskDependsOnAllQualityChecks() throws IOException {
        this.writeConsumerProject(UnifycodeGradlePluginFunctionalTest.JAVA_AND_UNIFYCODE, "");
        final BuildResult result = this.run("check", UnifycodeGradlePluginFunctionalTest.DRY_RUN);
        this.assertContains(
            result,
            UnifycodeGradlePluginFunctionalTest.UNIFYCODE_CHECK_SKIPPED,
            "Expected check to depend on unifycodeCheck."
        );
        this.assertContains(
            result, UnifycodeGradlePluginFunctionalTest.SPOTLESS_CHECK,
            "Expected spotlessCheck to be wired into check."
        );
        this.assertContains(
            result, UnifycodeGradlePluginFunctionalTest.PMD_MAIN, "Expected pmdMain to be wired into check."
        );
        this.assertContains(
            result,
            UnifycodeGradlePluginFunctionalTest.CHECKSTYLE_MAIN,
            "Expected checkstyleMain to be wired into check."
        );
    }

    @Test
    void formatTaskDependsOnSpotlessApply() throws IOException {
        this.writeConsumerProject(UnifycodeGradlePluginFunctionalTest.JAVA_AND_UNIFYCODE, "");
        final BuildResult result = this.run(
            UnifycodeGradlePluginFunctionalTest.FORMAT,
            UnifycodeGradlePluginFunctionalTest.DRY_RUN
        );
        this.assertContains(
            result,
            UnifycodeGradlePluginFunctionalTest.SPOTLESS_APPLY,
            "Expected unifycodeFormat task to depend on spotlessApply."
        );
    }

    @Test
    void unifycodeCheckDependsOnStaticAnalysisTasksOnly() throws IOException {
        this.writeConsumerProject(UnifycodeGradlePluginFunctionalTest.JAVA_AND_UNIFYCODE, "");
        final BuildResult result = this.run(
            UnifycodeGradlePluginFunctionalTest.UNIFYCODE_CHECK,
            UnifycodeGradlePluginFunctionalTest.DRY_RUN
        );
        this.assertContains(
            result,
            UnifycodeGradlePluginFunctionalTest.SPOTLESS_CHECK,
            "Expected unifycodeCheck to include spotlessCheck."
        );
        this.assertContains(
            result, UnifycodeGradlePluginFunctionalTest.PMD_MAIN, "Expected unifycodeCheck to include pmdMain."
        );
        this.assertContains(
            result,
            UnifycodeGradlePluginFunctionalTest.CHECKSTYLE_MAIN,
            "Expected unifycodeCheck to include checkstyleMain."
        );
        this.assertNotContains(result, ":test SKIPPED", "Expected unifycodeCheck not to include test task.");
    }

    @Test
    void checkstyleCanBeDisabled() throws IOException {
        this.writeConsumerProject(
            UnifycodeGradlePluginFunctionalTest.JAVA_AND_UNIFYCODE,
            this.disable("checkstyleEnabled")
        );
        final BuildResult result = this.run(
            UnifycodeGradlePluginFunctionalTest.UNIFYCODE_CHECK,
            UnifycodeGradlePluginFunctionalTest.DRY_RUN
        );
        this.assertContains(
            result, UnifycodeGradlePluginFunctionalTest.SPOTLESS_CHECK, "Expected spotlessCheck to remain enabled."
        );
        this.assertContains(result, UnifycodeGradlePluginFunctionalTest.PMD_MAIN, "Expected PMD to remain enabled.");
        this.assertNotContains(
            result,
            UnifycodeGradlePluginFunctionalTest.CHECKSTYLE_MAIN,
            "Expected Checkstyle to be disabled."
        );
        Assertions.assertFalse(
            Files.exists(this.testProjectDir.resolve("build/unifycode/checkstyle.xml")),
            "Expected checkstyle.xml not to be copied."
        );
    }

    @Test
    void pmdCanBeDisabled() throws IOException {
        this.writeConsumerProject(
            UnifycodeGradlePluginFunctionalTest.JAVA_AND_UNIFYCODE,
            this.disable("pmdEnabled")
        );
        final BuildResult result = this.run(
            UnifycodeGradlePluginFunctionalTest.UNIFYCODE_CHECK,
            UnifycodeGradlePluginFunctionalTest.DRY_RUN
        );
        this.assertContains(
            result, UnifycodeGradlePluginFunctionalTest.SPOTLESS_CHECK, "Expected spotlessCheck to remain enabled."
        );
        this.assertContains(
            result,
            UnifycodeGradlePluginFunctionalTest.CHECKSTYLE_MAIN,
            "Expected Checkstyle to remain enabled."
        );
        this.assertNotContains(result, UnifycodeGradlePluginFunctionalTest.PMD_MAIN, "Expected PMD to be disabled.");
        Assertions.assertFalse(
            Files.exists(this.testProjectDir.resolve("build/unifycode/pmd.xml")),
            "Expected pmd.xml not to be copied."
        );
    }

    @Test
    void spotlessCanBeDisabled() throws IOException {
        this.writeConsumerProject(
            UnifycodeGradlePluginFunctionalTest.JAVA_AND_UNIFYCODE,
            this.disable("spotlessEnabled")
        );
        final BuildResult format = this.run(
            UnifycodeGradlePluginFunctionalTest.FORMAT,
            UnifycodeGradlePluginFunctionalTest.DRY_RUN
        );
        final BuildResult check = this.run(
            UnifycodeGradlePluginFunctionalTest.UNIFYCODE_CHECK,
            UnifycodeGradlePluginFunctionalTest.DRY_RUN
        );
        this.assertNotContains(
            format,
            UnifycodeGradlePluginFunctionalTest.SPOTLESS_APPLY,
            "Expected Spotless apply task to be disabled."
        );
        this.assertNotContains(
            check,
            UnifycodeGradlePluginFunctionalTest.SPOTLESS_CHECK,
            "Expected Spotless check task to be disabled."
        );
        this.assertContains(check, UnifycodeGradlePluginFunctionalTest.PMD_MAIN, "Expected PMD to remain enabled.");
        this.assertContains(
            check,
            UnifycodeGradlePluginFunctionalTest.CHECKSTYLE_MAIN,
            "Expected Checkstyle to remain enabled."
        );
        Assertions.assertFalse(
            Files.exists(this.testProjectDir.resolve("build/unifycode/eclipse-java-formatter.xml")),
            "Expected Spotless formatter config not to be copied."
        );
    }

    @Test
    void helperTasksRemainAvailableWhenAllToolsAreDisabled() throws IOException {
        this.writeConsumerProject(
            UnifycodeGradlePluginFunctionalTest.JAVA_AND_UNIFYCODE,
            this.disableAll()
        );
        final BuildResult format = this.run(
            UnifycodeGradlePluginFunctionalTest.FORMAT,
            UnifycodeGradlePluginFunctionalTest.DRY_RUN
        );
        final BuildResult check = this.run(
            UnifycodeGradlePluginFunctionalTest.UNIFYCODE_CHECK,
            UnifycodeGradlePluginFunctionalTest.DRY_RUN
        );
        this.assertContains(
            format,
            ":unifycodeFormat SKIPPED",
            "Expected unifycodeFormat task to remain available."
        );
        this.assertContains(check, ":unifycodeCheck SKIPPED", "Expected unifycodeCheck task to remain available.");
        this.assertNotContains(
            format,
            UnifycodeGradlePluginFunctionalTest.SPOTLESS_APPLY,
            "Expected no Spotless apply task."
        );
        this.assertNotContains(
            check,
            UnifycodeGradlePluginFunctionalTest.SPOTLESS_CHECK,
            "Expected no Spotless check task."
        );
        this.assertNotContains(check, UnifycodeGradlePluginFunctionalTest.PMD_MAIN, "Expected no PMD task.");
        this.assertNotContains(
            check,
            UnifycodeGradlePluginFunctionalTest.CHECKSTYLE_MAIN,
            "Expected no Checkstyle task."
        );
    }

    @Test
    void pluginWorksWhenAppliedBeforeJava() throws IOException {
        this.writeConsumerProject(UnifycodeGradlePluginFunctionalTest.UNIFYCODE_AND_JAVA, "");
        final BuildResult result = this.run(
            UnifycodeGradlePluginFunctionalTest.UNIFYCODE_CHECK,
            UnifycodeGradlePluginFunctionalTest.DRY_RUN
        );
        this.assertContains(
            result, UnifycodeGradlePluginFunctionalTest.SPOTLESS_CHECK, "Expected Spotless to be configured."
        );
        this.assertContains(result, UnifycodeGradlePluginFunctionalTest.PMD_MAIN, "Expected PMD to be configured.");
        this.assertContains(
            result,
            UnifycodeGradlePluginFunctionalTest.CHECKSTYLE_MAIN,
            "Expected Checkstyle to be configured."
        );
    }

    @Test
    void pluginStaysDormantForNonJavaProject() throws IOException {
        this.writeConsumerProject(UnifycodeGradlePluginFunctionalTest.UNIFYCODE_ONLY, "");
        final BuildResult format = this.run(
            UnifycodeGradlePluginFunctionalTest.FORMAT,
            UnifycodeGradlePluginFunctionalTest.DRY_RUN
        );
        final BuildResult check = this.run(
            UnifycodeGradlePluginFunctionalTest.UNIFYCODE_CHECK,
            UnifycodeGradlePluginFunctionalTest.DRY_RUN
        );
        this.assertContains(format, ":unifycodeFormat SKIPPED", "Expected unifycodeFormat task to exist.");
        this.assertContains(check, ":unifycodeCheck SKIPPED", "Expected unifycodeCheck task to exist.");
        this.assertNotContains(
            format,
            UnifycodeGradlePluginFunctionalTest.SPOTLESS_APPLY,
            "Expected no Spotless apply task."
        );
        this.assertNotContains(
            check,
            UnifycodeGradlePluginFunctionalTest.SPOTLESS_CHECK,
            "Expected no Spotless check task."
        );
        this.assertNotContains(check, UnifycodeGradlePluginFunctionalTest.PMD_MAIN, "Expected no PMD task.");
        this.assertNotContains(
            check,
            UnifycodeGradlePluginFunctionalTest.CHECKSTYLE_MAIN,
            "Expected no Checkstyle task."
        );
    }

    private BuildResult run(final String... arguments) {
        return GradleRunner.create()
            .withProjectDir(this.testProjectDir.toFile())
            .withArguments(arguments)
            .withPluginClasspath()
            .build();
    }

    private void writeConsumerProject(final String plugins, final String extraBuildScript) throws IOException {
        this.writeFile("settings.gradle", "rootProject.name = 'plugin-functional-test'\n");
        this.writeFile(
            "build.gradle",
            "plugins {\n"
                + plugins
                + UnifycodeGradlePluginFunctionalTest.BLOCK_END
                + "\n"
                + "repositories {\n"
                + "    mavenCentral()\n"
                + UnifycodeGradlePluginFunctionalTest.BLOCK_END
                + "\n"
                + extraBuildScript
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

    private void assertContains(final BuildResult result, final String fragment, final String message) {
        Assertions
            .assertTrue(result.getOutput().contains(fragment), () -> message + "\nOutput:\n" + result.getOutput());
    }

    private void assertNotContains(final BuildResult result, final String fragment, final String message) {
        Assertions.assertFalse(
            result.getOutput().contains(fragment),
            () -> message + "\nOutput:\n" + result.getOutput()
        );
    }

    private String disable(final String tool) {
        return "unifycode {\n    " + tool + " = false\n" + UnifycodeGradlePluginFunctionalTest.BLOCK_END;
    }

    private String disableAll() {
        return "unifycode {\n"
            + "    checkstyleEnabled = false\n"
            + "    pmdEnabled = false\n"
            + "    spotlessEnabled = false\n"
            + UnifycodeGradlePluginFunctionalTest.BLOCK_END;
    }
}
