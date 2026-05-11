package fixture;

import java.io.IOException;

final class MethodDeclarationsFixture {
    void writeValues(final String firstVeryLongParameterName, final String secondVeryLongParameterName,
                     final String thirdVeryLongParameterName)
        // @todo #3:20min Improve wrapped `throws` indentation beyond the current body-level alignment.
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
