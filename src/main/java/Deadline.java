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

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + TaskDateTime.format(by) + ")";
    }
}
