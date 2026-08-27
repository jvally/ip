package friday.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** A task that must be completed by the given date or time. */
public class Deadline extends Task {
    private final LocalDateTime by;

    /** Parses the deadline immediately so tasks never contain unvalidated date text. */
    public Deadline(String description, String by) {
        super(description);
        this.by = TaskDateTime.parse(by);
    }

    public LocalDateTime getBy() {
        return by;
    }

    /** Matches the due date regardless of its time or the task's completion status. */
    @Override
    public boolean occursOn(LocalDate date) {
        return by.toLocalDate().equals(date);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + TaskDateTime.format(by) + ")";
    }
}
