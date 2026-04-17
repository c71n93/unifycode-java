package fixture;

final class MethodCallsFixture {
    void wrap(final String firstVeryLongParameterName, final String secondVeryLongParameterName,
              final String thirdVeryLongParameterName) {
        this.consume(
            firstVeryLongParameterName, secondVeryLongParameterName, thirdVeryLongParameterName
        );
    }

    private void consume(final String firstVeryLongParameterName, final String secondVeryLongParameterName,
                         final String thirdVeryLongParameterName) {
        if (firstVeryLongParameterName.isBlank()) {
            throw new IllegalStateException();
        }
    }
}
