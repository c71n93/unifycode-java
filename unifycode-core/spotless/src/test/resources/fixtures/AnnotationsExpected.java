package fixture;

import java.util.Map;

final class AnnotationsFixture {
    private void annotations(@Deprecated final String first, @SuppressWarnings("unused") final String second) {
        final Map<String, Integer> values = Map.of(first, 1, second, 2);
        if (values.containsKey(first) && values.containsKey(second)) {
            this.consume(first, second, String.valueOf(values.get(first)));
        }
    }

    private void consume(final String first, final String second, final String third) {
        if (!first.isBlank() && !second.isBlank() && !third.isBlank()) {
            System.out.println(first + second + third);
        }
    }
}
