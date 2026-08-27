import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Dependency-free regression tests for task-list operations and collection ownership. */
public class TaskListTest {
    public static void main(String[] args) {
        testAddAndDelete();
        testInvalidTaskNumbers();
        testCompletionChanges();
        testCollectionOwnership();
        testDateQueries();
        System.out.println("All 5 task-list test groups passed.");
    }

    /** All task types keep insertion order, and deletion updates the displayed task numbers. */
    private static void testAddAndDelete() {
        TaskList tasks = new TaskList();
        Task todo = new ToDo("read book");
        Task deadline = new Deadline("return book", "2019-12-02");
        Task event = new Event("meeting", "2019-12-02 14:00", "2019-12-02 16:00");
        tasks.add(todo);
        tasks.add(deadline);
        tasks.add(event);
        check(tasks.size() == 3 && tasks.get(1) == todo && tasks.get(3) == event,
                "Adding must preserve insertion order and use one-based task numbers.");
        check(tasks.delete(2) == deadline && tasks.size() == 2 && tasks.get(2) == event,
                "Deleting the middle task must return it and shift later tasks up.");
        check(tasks.delete(1) == todo && tasks.get(1) == event,
                "Deleting the first task must renumber the remaining task.");
        check(tasks.delete(1) == event && tasks.size() == 0,
                "Deleting the final task must leave an empty list.");
    }

    /** Invalid numbers must be rejected before any read or mutation can address the wrong task. */
    private static void testInvalidTaskNumbers() {
        TaskList empty = new TaskList();
        check(!empty.isValidTaskNumber(1), "An empty list has no valid task number.");
        expectInvalidNumber(() -> empty.delete(1));
        Task task = new ToDo("keep me");
        TaskList tasks = new TaskList(List.of(task));
        check(tasks.isValidTaskNumber(1), "The first task must be selectable.");
        for (int number : new int[]{Integer.MIN_VALUE, -1, 0, 2, Integer.MAX_VALUE}) {
            check(!tasks.isValidTaskNumber(number), "Accepted invalid task number: " + number);
            expectInvalidNumber(() -> tasks.get(number));
            expectInvalidNumber(() -> tasks.delete(number));
            expectInvalidNumber(() -> tasks.mark(number));
            expectInvalidNumber(() -> tasks.unmark(number));
        }
        check(tasks.size() == 1 && tasks.get(1) == task && !task.isDone(),
                "Rejected operations must leave the collection and task status unchanged.");
    }

    /** Repeated mark/unmark commands must report no change so callers can avoid redundant saves. */
    private static void testCompletionChanges() {
        TaskList tasks = new TaskList(List.of(new ToDo("read book"), new ToDo("buy bread")));
        check(!tasks.unmark(1), "Unmarking a new task must not report a change.");
        check(tasks.mark(1) && tasks.get(1).isDone(), "Marking must change the selected task.");
        check(!tasks.mark(1), "Marking an already completed task must not report a change.");
        check(tasks.unmark(1) && !tasks.get(1).isDone(), "Unmarking must clear completion.");
        check(!tasks.unmark(1) && !tasks.get(2).isDone(),
                "Repeated unmarking must leave both task statuses unchanged.");
    }

    /** Loaded and exported lists cannot change membership behind TaskList's operations. */
    private static void testCollectionOwnership() {
        Task completed = new ToDo("already finished");
        completed.markAsDone();
        List<Task> loaded = new ArrayList<>(List.of(completed));
        TaskList tasks = new TaskList(loaded);
        loaded.clear();
        check(tasks.size() == 1 && tasks.get(1).isDone(),
                "Loading must preserve tasks and status independently of the source list's membership.");
        List<Task> exported = tasks.toList();
        try {
            exported.clear();
            throw new AssertionError("The exported list must not allow structural changes.");
        } catch (UnsupportedOperationException expected) {
            // Storage can read the copy but cannot add or remove tasks through it.
        }
        tasks.add(new ToDo("new task"));
        tasks.delete(1);
        check(exported.size() == 1 && exported.get(0) == completed,
                "Later list changes must not alter an earlier exported copy.");
        check(tasks.size() == 1 && tasks.get(1).getDescription().equals("new task"),
                "TaskList must remain mutable even when initialized from an immutable list or exported.");
    }

    /** Filtering keeps original task numbers, including gaps, and reflects deletion and completion. */
    private static void testDateQueries() {
        LocalDate day = LocalDate.of(2019, 12, 2);
        TaskList tasks = new TaskList(List.of(
                new ToDo("buy bread"),
                new Deadline("return book", "2019-12-02 18:00"),
                new Deadline("later", "2019-12-04"),
                new Event("conference", "2019-12-01 10:00", "2019-12-03 00:00")));
        tasks.mark(4);
        check(tasks.findTaskNumbersOn(day).equals(List.of(2, 4)),
                "Results must retain original list numbers and include completed events.");
        tasks.delete(1);
        check(tasks.findTaskNumbersOn(day).equals(List.of(1, 3)),
                "Results must reflect the new task numbers after deletion.");
        check(tasks.findTaskNumbersOn(day.plusDays(10)).isEmpty(), "Unmatched dates must return no numbers.");
        check(tasks.size() == 3 && !tasks.get(1).isDone() && tasks.get(3).isDone(),
                "Date queries must not alter task membership or completion status.");
        check(new TaskList().findTaskNumbersOn(day).isEmpty(), "An empty list must have no date matches.");
    }

    /** Confirms that a task-number operation rejects an out-of-range selection. */
    private static void expectInvalidNumber(Runnable operation) {
        try {
            operation.run();
            throw new AssertionError("Accepted an invalid task number.");
        } catch (IndexOutOfBoundsException expected) {
            // The console checks validity before invoking the operation and supplies the user-facing error.
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
