package fixture;

/**
 * Ctor.
 *
 * @param name
 *            sample name
 * @param size
 *            sample size
 */
final class DocumentationFixture {
    private final String name;
    private final int size;

    DocumentationFixture(final String name, final int size) {
        this.name = name;
        this.size = size;
    }

    /**
     * Formatted method.
     *
     * @param writer
     *            buffered writer
     * @param values
     *            values to write
     * @throws IllegalStateException
     *             if state bad
     */
    void write(final String writer, final int values) throws IllegalStateException {
        if (writer.isBlank() && values > 0) {
            throw new IllegalStateException();
        }
    }
}
