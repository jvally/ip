import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/** Dependency-free regression tests for date parsing and display. */
public class DateTimeTest {
    public static void main(String[] args) {
        testSupportedFormats();
        testInvalidDates();
        testDisplay();
        System.out.println("All 3 date/time test groups passed.");
    }

    /** Alternate input formats must produce the same actual date-time value. */
    private static void testSupportedFormats() {
        LocalDateTime expected = LocalDateTime.of(2019, 12, 2, 18, 0);
        for (String input : List.of("2019-12-02 18:00", "2/12/2019 1800", "02/12/2019 1800",
                "2019-12-02T18:00")) {
            check(TaskDateTime.parse(input).equals(expected), "Wrong parsed date: " + input);
        }
        check(TaskDateTime.parse("2019-10-15").equals(LocalDateTime.of(2019, 10, 15, 0, 0)),
                "Date-only input must mean midnight.");
        check(TaskDateTime.parse("2024-02-29 23:59").equals(LocalDateTime.of(2024, 2, 29, 23, 59)),
                "A valid leap day must be accepted.");
        check(TaskDateTime.parse("29/2/2024 0000").equals(LocalDateTime.of(2024, 2, 29, 0, 0)),
                "Day-first input must also accept valid leap days and midnight.");
    }

    /** Invalid days and times must fail rather than being silently normalized. */
    private static void testInvalidDates() {
        for (String input : List.of("", "Sunday", "2019-02-29", "2019-04-31", "2019-13-01",
                "2019-00-01", "2019-12-00", "2019-1-2", "2019-12-02 24:00", "2019-12-02 18:60",
                "2019-02-29 18:00", "29/2/2019 1800", "31/4/2019 1800", "2/12/2019 2400",
                "2/12/2019 1860", "2/12/2019 180", "2019-12-02T18:00:30", "2019-12-02 18:00 extra")) {
            try {
                TaskDateTime.parse(input);
                throw new AssertionError("Accepted invalid date/time: " + input);
            } catch (IllegalArgumentException expected) {
                check(expected.getMessage().startsWith("Invalid date/time."), "Explain the supported formats.");
            }
        }
    }

    /** Display is readable, locale-independent, and distinct from the storage format. */
    private static void testDisplay() {
        Locale original = Locale.getDefault();
        Locale.setDefault(Locale.FRENCH);
        try {
            check(TaskDateTime.format(TaskDateTime.parse("2019-10-15")).equals("Oct 15 2019"),
                    "Date-only output must use English month names.");
            check(TaskDateTime.format(TaskDateTime.parse("2/12/2019 1800")).equals("Dec 02 2019, 18:00"),
                    "Timed output must preserve hours and minutes.");
            check(TaskDateTime.format(TaskDateTime.parse("2019-12-02 00:00")).equals("Dec 02 2019"),
                    "Midnight must be displayed consistently with date-only input.");
        } finally {
            Locale.setDefault(original);
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
