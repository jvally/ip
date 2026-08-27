package friday.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Owns the ordered task collection and its operations, without doing console or file I/O.
 * All task numbers are one-based, matching the numbers shown to the user.
 */
public class TaskList {
    private final List<Task> tasks;

    public TaskList() {
        this(List.of());
    }

    /** Copies the initial list so adding or removing tasks elsewhere cannot change this collection. */
    public TaskList(List<Task> initialTasks) {
        tasks = new ArrayList<>(initialTasks);
    }

    public int size() {
        return tasks.size();
    }

    public boolean isValidTaskNumber(int taskNumber) {
        return taskNumber >= 1 && taskNumber <= tasks.size();
    }

    public void add(Task task) {
        tasks.add(task);
    }

    /** Returns the task at the given one-based number. */
    public Task get(int taskNumber) {
        return tasks.get(toIndex(taskNumber));
    }

    /** Removes and returns a task; later tasks move up by one number. */
    public Task delete(int taskNumber) {
        return tasks.remove(toIndex(taskNumber));
    }

    /** Marks a task done, returning true only when the status changed and needs saving. */
    public boolean mark(int taskNumber) {
        Task task = get(taskNumber);
        if (task.isDone()) {
            return false;
        }
        task.markAsDone();
        return true;
    }

    /** Marks a task not done, returning true only when the status changed and needs saving. */
    public boolean unmark(int taskNumber) {
        Task task = get(taskNumber);
        if (!task.isDone()) {
            return false;
        }
        task.unmarkAsDone();
        return true;
    }

    /** Returns matching tasks' original list numbers in order, without changing the collection. */
    public List<Integer> findTaskNumbersOn(LocalDate date) {
        List<Integer> matches = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).occursOn(date)) {
                matches.add(i + 1);
            }
        }
        return matches;
    }

    /**
     * Returns an unmodifiable copy of the current list for storage.
     * Membership and order are copied; the mutable task objects themselves are shared.
     */
    public List<Task> toList() {
        return List.copyOf(tasks);
    }

    /** Validates a user-facing task number before converting it to an internal list index. */
    private int toIndex(int taskNumber) {
        if (!isValidTaskNumber(taskNumber)) {
            throw new IndexOutOfBoundsException("Invalid task number: " + taskNumber);
        }
        return taskNumber - 1;
    }
}
