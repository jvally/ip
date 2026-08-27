import java.time.LocalDateTime;

/** A task scheduled between a start and an end time. */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;

    /** Parses both endpoints and rejects a backwards interval; equal endpoints are allowed. */
    public Event(String description, String from, String to) {
        super(description);
        this.from = TaskDateTime.parse(from);
        this.to = TaskDateTime.parse(to);
        if (this.to.isBefore(this.from)) {
            throw new IllegalArgumentException("An event cannot end before it starts.");
        }
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + TaskDateTime.format(from)
                + " to: " + TaskDateTime.format(to) + ")";
    }
}
