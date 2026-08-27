import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/** Dependency-free regression tests for date parsing and display. */
public class DateTimeTest {
    public static void main(String[] args) {
        testSupportedFormats();
        testInvalidDates();
        testDisplay();
        testTaskDates();
        testDateMatching();
        System.out.println("All 5 date/time test groups passed.");
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

    /** Task objects expose real date-time values and enforce chronological event endpoints. */
    private static void testTaskDates() {
        Deadline deadline = new Deadline("return book", "2/12/2019 1800");
        check(deadline.getBy().equals(LocalDateTime.of(2019, 12, 2, 18, 0)),
                "Deadlines must store LocalDateTime values.");
        Event event = new Event("overnight", "2019-12-02 23:00", "2019-12-03 01:00");
        check(event.getFrom().equals(LocalDateTime.of(2019, 12, 2, 23, 0))
                && event.getTo().equals(LocalDateTime.of(2019, 12, 3, 1, 0)),
                "Events must store both endpoints as LocalDateTime values.");
        Event instant = new Event("reminder", "2019-12-02", "2019-12-02");
        check(instant.getFrom().equals(instant.getTo()), "Equal endpoints must be allowed.");
        try {
            new Event("backwards", "2019-12-03", "2019-12-02");
            throw new AssertionError("Accepted an event ending before its start.");
        } catch (IllegalArgumentException expected) {
            check(expected.getMessage().equals("An event cannot end before it starts."),
                    "Explain the invalid interval.");
        }
    }

    /** Date queries include completed tasks and both event boundary dates, but exclude undated todos. */
    private static void testDateMatching() {
        LocalDate day = LocalDate.of(2019, 12, 2);
        check(!new ToDo("read book").occursOn(day), "Todos must not match a date.");
        Deadline deadline = new Deadline("return book", "2019-12-02 18:00");
        deadline.markAsDone();
        check(deadline.occursOn(day) && !deadline.occursOn(day.plusDays(1)),
                "A deadline must match only its due date, even when done.");
        Event event = new Event("conference", "2019-12-01 10:00", "2019-12-03 00:00");
        check(event.occursOn(day.minusDays(1)) && event.occursOn(day) && event.occursOn(day.plusDays(1)),
                "An event must include its start date, interior dates, and end date.");
        check(!event.occursOn(day.minusDays(2)) && !event.occursOn(day.plusDays(2)),
                "An event must not match outside its date range.");
        Event instant = new Event("reminder", "2019-12-02", "2019-12-02");
        check(instant.occursOn(day) && !instant.occursOn(day.plusDays(1)),
                "An event with equal endpoints must match its single date.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
