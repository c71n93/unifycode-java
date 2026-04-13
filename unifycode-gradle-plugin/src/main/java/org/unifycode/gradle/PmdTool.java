package org.unifycode.gradle;

import java.io.File;
import java.util.Collections;
import org.gradle.api.Project;
import org.gradle.api.plugins.quality.PmdExtension;

final class PmdTool {
    private static final String RESOURCE = "unifycode/pmd/pmd.xml";

    private final Project project;
    private final UnifycodeResources resources;

    PmdTool(final Project project, final UnifycodeResources resources) {
        this.project = project;
        this.resources = resources;
    }

    PmdTool(final Project project) {
        this.project = project;
        this.resources = new UnifycodeResources(project);
    }

    void configure() {
        this.project.getPluginManager().apply("pmd");
        final File config = this.resources.copy(PmdTool.RESOURCE);
        this.project.getExtensions().configure(PmdExtension.class, extension -> {
            extension.setToolVersion("7.0.0");
            extension.setConsoleOutput(true);
            extension.setRuleSets(Collections.emptyList());
            extension.setRuleSetFiles(this.project.files(config));
        });
    }
}
