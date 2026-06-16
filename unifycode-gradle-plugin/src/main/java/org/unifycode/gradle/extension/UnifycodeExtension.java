package org.unifycode.gradle.extension;

import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.model.ObjectFactory;

/**
 * Gradle extension for Unifycode configuration.
 */
@SuppressWarnings("PMD.DataClass")
public class UnifycodeExtension {
    /**
     * Checkstyle policy.
     */
    private final QualityToolPolicy checkstyle;

    /**
     * PMD policy.
     */
    private final QualityToolPolicy pmd;

    /**
     * New extension with nested quality tool policies.
     *
     * @param objects gradle object factory.
     */
    @Inject
    public UnifycodeExtension(final ObjectFactory objects) {
        this.checkstyle = objects.newInstance(QualityToolPolicy.class);
        this.pmd = objects.newInstance(QualityToolPolicy.class);
    }

    /**
     * Checkstyle policy.
     *
     * @return checkstyle policy.
     */
    public QualityToolPolicy getCheckstyle() {
        return this.checkstyle;
    }

    /**
     * Configures Checkstyle policy.
     *
     * @param action policy action.
     */
    public void checkstyle(final Action<? super QualityToolPolicy> action) {
        action.execute(this.checkstyle);
    }

    /**
     * PMD policy.
     *
     * @return PMD policy.
     */
    public QualityToolPolicy getPmd() {
        return this.pmd;
    }

    /**
     * Configures PMD policy.
     *
     * @param action policy action.
     */
    public void pmd(final Action<? super QualityToolPolicy> action) {
        action.execute(this.pmd);
    }
}
