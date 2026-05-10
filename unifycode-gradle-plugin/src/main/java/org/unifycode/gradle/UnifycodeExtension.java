package org.unifycode.gradle;

import org.gradle.api.provider.Property;

public abstract class UnifycodeExtension {

    @SuppressWarnings({
        "PMD.ConstructorCallsOverridableMethod",
        "PMD.ConstructorOnlyInitializesOrCallOtherConstructors"
    })
    public UnifycodeExtension() {
        this.getCheckstyleEnabled().convention(true);
        this.getPmdEnabled().convention(true);
        this.getSpotlessEnabled().convention(true);
    }

    public abstract Property<Boolean> getCheckstyleEnabled();

    public abstract Property<Boolean> getPmdEnabled();

    public abstract Property<Boolean> getSpotlessEnabled();
}
