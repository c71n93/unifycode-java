package io.github.c71n93.unifycode.gradle.extension;

import javax.inject.Inject;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

/**
 * Quality tool policy.
 */
public class QualityToolPolicy {
    /**
     * Strictness flag.
     */
    private final Property<Boolean> strict;

    /**
     * New policy with Gradle-managed properties.
     *
     * @param objects gradle object factory.
     */
    @Inject
    public QualityToolPolicy(final ObjectFactory objects) {
        this.strict = objects.property(Boolean.class).convention(true);
    }

    /**
     * Strictness property.
     *
     * @return strictness property.
     */
    public Property<Boolean> getStrict() {
        return this.strict;
    }

    /**
     * Non-failing violations flag.
     *
     * @return non-failing violations flag.
     */
    public boolean ignoresFailures() {
        return !this.strict.get();
    }
}
