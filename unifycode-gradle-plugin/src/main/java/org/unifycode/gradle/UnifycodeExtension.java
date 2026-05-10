package org.unifycode.gradle;

import org.gradle.api.provider.Property;

/**
 * Gradle extension for enabling and disabling Unifycode tools.
 */
public abstract class UnifycodeExtension {

    /**
     * Extension instance with all tools enabled by default.
     */
    @SuppressWarnings({
        "PMD.ConstructorCallsOverridableMethod",
        "PMD.ConstructorOnlyInitializesOrCallOtherConstructors"
    })
    public UnifycodeExtension() {
        this.getCheckstyleEnabled().convention(true);
        this.getPmdEnabled().convention(true);
        this.getSpotlessEnabled().convention(true);
    }

    /**
     * Checkstyle enablement flag.
     *
     * @return Checkstyle enablement property
     */
    public abstract Property<Boolean> getCheckstyleEnabled();

    /**
     * PMD enablement flag.
     *
     * @return PMD enablement property
     */
    public abstract Property<Boolean> getPmdEnabled();

    /**
     * Spotless enablement flag.
     *
     * @return Spotless enablement property
     */
    public abstract Property<Boolean> getSpotlessEnabled();
}
