package io.github.c71n93.unifycode.gradle;

import java.io.IOException;
import java.nio.file.Path;
import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Functional tests for the Unifycode Gradle plugin.
 */
final class UnifycodeGradlePluginFunctionalTest {
    /**
     * Default Java project build fixture.
     */
    private static final String JAVA_AND_UNIFYCODE = "java-and-unifycode";

    /**
     * Checkstyle missing type Javadoc rule name.
     */
    private static final String MISSING_JAVADOC_TYPE = "MissingJavadocType";

    /**
     * PMD public static method rule name.
     */
    private static final String PROHIBIT_PUBLIC_STATIC_METHODS = "ProhibitPublicStaticMethods";

    /**
     * Temporary consumer project directory.
     */
    @TempDir
    private Path testProjectDir;

    @Test
    void checkTaskDependsOnAllQualityChecks() throws IOException {
        final GradleFixtureProject project = this.project();
        project.writeBuild(UnifycodeGradlePluginFunctionalTest.JAVA_AND_UNIFYCODE);

        final BuildResult result = project.succeeds("check");

        this.assertTaskScheduled(result, ":unifycodeCheck");
        this.assertTaskScheduled(result, ":spotlessCheck");
        this.assertTaskScheduled(result, ":pmdMain");
        this.assertTaskScheduled(result, ":checkstyleMain");
    }

    @Test
    void unifycodeCheckDependsOnStaticAnalysisTasksOnly() throws IOException {
        final GradleFixtureProject project = this.project();
        project.writeBuild(UnifycodeGradlePluginFunctionalTest.JAVA_AND_UNIFYCODE);

        final BuildResult result = project.succeeds("unifycodeCheck");

        this.assertTaskScheduled(result, ":spotlessCheck");
        this.assertTaskScheduled(result, ":pmdMain");
        this.assertTaskScheduled(result, ":checkstyleMain");
        this.assertTaskNotScheduled(result, ":test");
    }

    @Test
    void pluginWorksWhenAppliedBeforeJava() throws IOException {
        final GradleFixtureProject project = this.project();
        project.writeBuild("unifycode-and-java");

        final BuildResult result = project.succeeds("unifycodeCheck");

        this.assertTaskScheduled(result, ":spotlessCheck");
        this.assertTaskScheduled(result, ":pmdMain");
        this.assertTaskScheduled(result, ":checkstyleMain");
    }

    // @todo #2:30min Move Checkstyle rule-behavior coverage (3 tests below) to the checkstyle subproject.
    @Test
    void checkstyleIgnoresJavadocsForActualTests() throws IOException {
        final GradleFixtureProject project = this.project();
        project.writeBuild(UnifycodeGradlePluginFunctionalTest.JAVA_AND_UNIFYCODE);
        project.writeTestPackageInfo();
        project.writeTestSource("app-test-with-type-javadoc");

        final BuildResult result = project.succeeds("checkstyleTest");

        this.assertTaskScheduled(result, ":checkstyleTest");
    }

    @Test
    void checkstyleRequiresTypeJavadocsForActualTests() throws IOException {
        final GradleFixtureProject project = this.project();
        project.writeBuild(UnifycodeGradlePluginFunctionalTest.JAVA_AND_UNIFYCODE);
        project.writeTestPackageInfo();
        project.writeTestSource("app-test-missing-type-javadoc");

        project.fails("checkstyleTest");

        Assertions.assertTrue(
            project.checkstyleTestReport().contains(UnifycodeGradlePluginFunctionalTest.MISSING_JAVADOC_TYPE),
            "Expected actual test classes to require type Javadocs."
        );
    }

    @Test
    void checkstyleRequiresJavadocsForTestInfrastructure() throws IOException {
        final GradleFixtureProject project = this.project();
        project.writeBuild(UnifycodeGradlePluginFunctionalTest.JAVA_AND_UNIFYCODE);
        project.writeTestPackageInfo();
        project.writeFixture(
            "test/fake-app-missing-method-javadoc.java",
            "src/test/java/demo/FakeApp.java"
        );

        project.fails("checkstyleTest");

        Assertions.assertTrue(
            project.checkstyleTestReport().contains("MissingJavadoc"),
            "Expected test infrastructure to require Javadocs."
        );
    }

    @Test
    void nonStrictCheckstyleReportsViolationsWithoutFailingBuild() throws IOException {
        final GradleFixtureProject project = this.project();
        project.writeBuild("non-strict-checkstyle");
        project.writeMainSource("missing-type-javadoc");

        project.succeeds("checkstyleMain");

        Assertions.assertTrue(
            project.checkstyleMainReport().contains(UnifycodeGradlePluginFunctionalTest.MISSING_JAVADOC_TYPE),
            "Expected non-strict Checkstyle to write violation details."
        );
    }

    @Test
    void strictCheckstyleFailsBuildOnViolations() throws IOException {
        final GradleFixtureProject project = this.project();
        project.writeBuild(UnifycodeGradlePluginFunctionalTest.JAVA_AND_UNIFYCODE);
        project.writeMainSource("missing-type-javadoc");

        project.fails("checkstyleMain");

        Assertions.assertTrue(
            project.checkstyleMainReport().contains(UnifycodeGradlePluginFunctionalTest.MISSING_JAVADOC_TYPE),
            "Expected strict Checkstyle to write violation details."
        );
    }

    @Test
    void nonStrictPmdPolicyAllowsBuildWhileStillWritingReport() throws IOException {
        final GradleFixtureProject project = this.project();
        project.writeBuild("non-strict-pmd");
        project.writeMainSource("pmd-public-static-method");

        project.succeeds("pmdMain");

        Assertions.assertTrue(
            project.pmdMainReport().contains(UnifycodeGradlePluginFunctionalTest.PROHIBIT_PUBLIC_STATIC_METHODS),
            "Expected non-strict PMD policy to allow the build while still writing a report."
        );
    }

    @Test
    void strictPmdPolicyFailsBuildWhenPmdReportsViolation() throws IOException {
        final GradleFixtureProject project = this.project();
        project.writeBuild(UnifycodeGradlePluginFunctionalTest.JAVA_AND_UNIFYCODE);
        project.writeMainSource("pmd-public-static-method");

        project.fails("pmdMain");

        Assertions.assertTrue(
            project.pmdMainReport().contains(UnifycodeGradlePluginFunctionalTest.PROHIBIT_PUBLIC_STATIC_METHODS),
            "Expected strict PMD policy to fail after PMD reports a violation."
        );
    }

    @Test
    void strictnessConfiguredBeforeJavaPluginIsHonored() throws IOException {
        final GradleFixtureProject project = this.project();
        project.writeBuild("non-strict-before-java");
        project.writeMainSource("pmd-public-static-method");

        project.succeeds("checkstyleMain");
        project.succeeds("pmdMain");

        Assertions.assertTrue(
            project.checkstyleMainReport().contains(UnifycodeGradlePluginFunctionalTest.MISSING_JAVADOC_TYPE),
            "Expected Checkstyle policy configured before Java to be honored."
        );
        Assertions.assertTrue(
            project.pmdMainReport().contains(UnifycodeGradlePluginFunctionalTest.PROHIBIT_PUBLIC_STATIC_METHODS),
            "Expected PMD policy configured before Java to be honored."
        );
    }

    private GradleFixtureProject project() {
        return new GradleFixtureProject(this.testProjectDir);
    }

    private void assertTaskScheduled(final BuildResult result, final String taskPath) {
        Assertions.assertNotNull(
            result.task(taskPath),
            () -> "Expected " + taskPath + " to be scheduled.\nOutput:\n" + result.getOutput()
        );
    }

    private void assertTaskNotScheduled(final BuildResult result, final String taskPath) {
        Assertions.assertNull(result.task(taskPath), "Expected " + taskPath + " not to be scheduled.");
    }
}
