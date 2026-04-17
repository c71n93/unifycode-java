package fixture;

import java.io.IOException;

final class MethodDeclarationsFixture {
    void writeValues(final String firstVeryLongParameterName, final String secondVeryLongParameterName,
                     final String thirdVeryLongParameterName)
        // TODO: Better extra indent for wrapped `throws` than body-level indent.
        throws IOException {
        if (firstVeryLongParameterName.isBlank()) {
            throw new IOException();
        }
    }

    void writeRow(final String firstVeryLongParameterName, final String secondVeryLongParameterName,
                  final String thirdVeryLongParameterName, final String fourthVeryLongParameterName)
        throws IOException {
        if (secondVeryLongParameterName.isBlank()) {
            throw new IOException();
        }
    }
}
