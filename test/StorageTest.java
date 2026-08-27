import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import friday.task.Deadline;
import friday.task.Event;
import friday.task.Task;
import friday.task.TaskDateTime;
import friday.task.ToDo;

/** Dependency-free storage regression tests; run with Java 25.0.3.fx-zulu. */
public class StorageTest {
    public static void main(String[] args) throws IOException {
        Path directory = Files.createTempDirectory("friday-storage-test-");
        try {
            testMissingFile(directory);
            testRoundTrip(directory);
            testInvalidRecords(directory);
            testLargeList(directory);
            testFailedSave(directory);
            testLegacyDateRecords(directory);
            System.out.println("All 6 storage test groups passed.");
        } finally {
            try (var paths = Files.walk(directory)) {
                for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(path);
                }
            }
        }
    }

    /** A missing file and missing parent folders are normal on the first run. */
    private static void testMissingFile(Path directory) throws IOException {
        Path file = directory.resolve("missing/nested/friday.txt");
        Storage storage = new Storage(file);
        check(storage.load().isEmpty(), "Missing data must load as an empty list.");
        check(!Files.exists(file.getParent()), "Loading must not require creating folders.");
        storage.save(List.of(new ToDo("first task")));
        check(storage.load().size() == 1, "Saving must create missing parent folders.");
    }

    /** All types, status values, Unicode, and escaped characters survive a save/load cycle. */
    private static void testRoundTrip(Path directory) throws IOException {
        Storage storage = new Storage(directory.resolve("round-trip.txt"));
        List<Task> tasks = new ArrayList<>(List.of(
                new ToDo("read | books \\ notes\nnext\rline café 读书"),
                new Deadline("return | book \\ café", "2/12/2019 1800"),
                new Event("meeting\nnext\rline", "2019-12-02 14:00", "2019-12-03 16:00")));
        tasks.get(0).markAsDone();
        tasks.get(2).markAsDone();
        storage.save(tasks);
        String saved = Files.readString(directory.resolve("round-trip.txt"));
        check(saved.contains("|2019-12-02T18:00"), "Store dates in canonical ISO format.");
        List<Task> loaded = storage.load();
        check(loaded.size() == tasks.size(), "Every task must load.");
        for (int i = 0; i < tasks.size(); i++) {
            check(loaded.get(i).getClass() == tasks.get(i).getClass(), "Task type must survive.");
            check(loaded.get(i).toString().equals(tasks.get(i).toString()), "Task fields must survive.");
        }
        check(((Deadline) loaded.get(1)).getBy().equals(((Deadline) tasks.get(1)).getBy()),
                "The actual deadline date-time must survive.");
        check(((Event) loaded.get(2)).getFrom().equals(((Event) tasks.get(2)).getFrom())
                && ((Event) loaded.get(2)).getTo().equals(((Event) tasks.get(2)).getTo()),
                "Both actual event endpoints must survive.");
        loaded.get(0).unmarkAsDone();
        loaded.get(1).markAsDone();
        loaded.remove(2);
        storage.save(loaded);
        List<Task> changed = storage.load();
        check(changed.size() == 2 && !changed.get(0).isDone() && changed.get(1).isDone(),
                "Changed statuses and deletion must replace the old snapshot.");
        storage.save(List.of());
        check(storage.load().isEmpty(), "Deleting all tasks must produce an empty save.");
    }

    /** Malformed records and invalid UTF-8 are rejected without changing the original bytes. */
    private static void testInvalidRecords(Path directory) throws IOException {
        Path file = directory.resolve("corrupt.txt");
        Storage storage = new Storage(file);
        for (String invalid : List.of("", "nonsense", "Q|0|unknown", "T|2|bad status",
                "T|true|bad status", "T|0|", "T|0|   ", "T|0|extra|field", "D|0|missing date",
                "D|0|empty date|", "E|0|missing end|start", "E|0|event||end", "E|0|event|start|end|extra",
                "T|0|bad\\q", "T|0|trailing\\", "D|0|old deadline|Sunday",
                "D|0|impossible date|2019-02-29", "D|0|bad time|2019-12-02T24:00",
                "E|0|old event|Mon 2pm|4pm", "E|0|backwards|2019-12-03|2019-12-02",
                "E|0|bad end|2019-12-02|2019-02-29")) {
            String original = "T|0|valid first record\n" + invalid + "\n";
            Files.writeString(file, original);
            try {
                storage.load();
                throw new AssertionError("Accepted invalid record: " + invalid);
            } catch (IOException e) {
                check(e.getMessage().contains("line 2"), "Corruption must identify the offending line.");
                check(Files.readString(file).equals(original), "Loading must preserve the corrupt file.");
            }
        }
        Files.write(file, new byte[]{(byte) 0xC3, (byte) 0x28});
        try {
            storage.load();
            throw new AssertionError("Accepted invalid UTF-8.");
        } catch (IOException expected) {
            check(Files.size(file) == 2, "Invalid UTF-8 must not be overwritten.");
        }
    }

    /** Saved lists are not limited to the old array's 100 slots. */
    private static void testLargeList(Path directory) throws IOException {
        Storage storage = new Storage(directory.resolve("large.txt"));
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < 125; i++) {
            tasks.add(new ToDo("task " + i));
        }
        storage.save(tasks);
        List<Task> loaded = storage.load();
        check(loaded.size() == 125 && loaded.get(124).getDescription().equals("task 124"),
                "Loading must not truncate large lists.");
    }

    /** Failed writes leave existing data intact and clean up incomplete temporary snapshots. */
    private static void testFailedSave(Path directory) throws IOException {
        Path parentFile = directory.resolve("not-a-directory");
        Files.writeString(parentFile, "unchanged");
        try {
            new Storage(parentFile.resolve("friday.txt")).save(List.of(new ToDo("task")));
            throw new AssertionError("Saving through a file as a parent must fail.");
        } catch (IOException expected) {
            check(Files.readString(parentFile).equals("unchanged"), "Parent file must be untouched.");
        }

        Path destination = directory.resolve("blocked/friday.txt");
        Files.createDirectories(destination);
        Files.writeString(destination.resolve("keep.txt"), "unchanged");
        try {
            new Storage(destination).save(List.of(new ToDo("task")));
            throw new AssertionError("Replacing a nonempty directory must fail.");
        } catch (IOException expected) {
            check(Files.readString(destination.resolve("keep.txt")).equals("unchanged"),
                    "Failed replacement must not remove existing data.");
        }
        try (var files = Files.list(destination.getParent())) {
            check(files.count() == 1, "Failed saves must clean up their temporary files.");
        }

        Path file = directory.resolve("preserved.txt");
        Storage storage = new Storage(file);
        storage.save(List.of(new ToDo("keep this task")));
        String original = Files.readString(file);
        try {
            storage.save(List.of(new ToDo("partial write"), new Task("unsupported")));
            throw new AssertionError("An unsupported task type must fail to serialize.");
        } catch (IllegalArgumentException expected) {
            check(Files.readString(file).equals(original), "A partial write must not replace a valid save.");
        }
    }

    /** Older records with unambiguous supported dates can load and be saved in canonical form. */
    private static void testLegacyDateRecords(Path directory) throws IOException {
        Path file = directory.resolve("legacy-dates.txt");
        Files.writeString(file, "D|1|return book|2019-12-02\n"
                + "E|0|meeting|2/12/2019 1800|2019-12-02 20:00\n");
        Storage storage = new Storage(file);
        List<Task> loaded = storage.load();
        check(((Deadline) loaded.get(0)).getBy().equals(TaskDateTime.parse("2019-12-02")),
                "Older ISO date-only fields must remain readable.");
        check(loaded.get(0).isDone(), "Loading dates must preserve completion status.");
        storage.save(loaded);
        check(Files.readString(file).equals("D|1|return book|2019-12-02T00:00" + System.lineSeparator()
                + "E|0|meeting|2019-12-02T18:00|2019-12-02T20:00" + System.lineSeparator()),
                "Saving must normalize supported older date fields without losing time information.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
