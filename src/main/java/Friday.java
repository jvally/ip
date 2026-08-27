import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Friday chatbot entry point.
 * Uses inheritance for shared Task behavior and polymorphism to store multiple task types in one list.
 * Delegates console interactions to Ui and persistence to Storage.
 */
public class Friday {
    private static final String UNKNOWN_COMMAND_MESSAGE = "Sir, I don't know what you are saying :-(";
    private static final String EMPTY_TODO_MESSAGE = "Sir, description of a todo cannot be empty.";
    private static final String INVALID_MARK_MESSAGE = "Sir, Invalid mark format. Use: mark TASK_NUMBER";
    private static final String INVALID_UNMARK_MESSAGE = "Sir, Invalid unmark format. Use: unmark TASK_NUMBER";
    private static final String INVALID_DELETE_MESSAGE = "Sir, Invalid delete format. Use: delete TASK_NUMBER";
    private static final String INVALID_TASK_NUMBER_MESSAGE = "Sir, The task number is invalid.";
    private static final Path DATA_FILE = Path.of("data", "friday.txt");

    /** Loads saved tasks and processes commands, saving after each task-list change. */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage(DATA_FILE);
        List<Task> tasks = new ArrayList<>();
        boolean savingEnabled = true;

        ui.showWelcome();
        try {
            tasks = storage.load();
        } catch (IOException | SecurityException e) {
            savingEnabled = false;
            ui.showLoadingError();
        }
        ui.showLine();

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();

            ui.showLine();
            if (command.equals("bye")) {
                ui.showGoodbye();
                break;
            } else if (command.equals("hello")) {
                ui.showGreeting();
                continue;
            } else if (command.equals("thanks")) {
                ui.showThanks();
                continue;
            } else if (command.equals("help")) {
                ui.showHelp();
                continue;
            } else if (command.equals("todo") || command.startsWith("todo ")) {
                String description = parseCommandBody(command, "todo ");
                if (!description.isEmpty()) {
                    addTask(tasks, new ToDo(description), ui);
                    saveTasks(storage, tasks, savingEnabled, ui);
                } else {
                    ui.showError(EMPTY_TODO_MESSAGE);
                }
                continue;
            } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                String commandBody = parseCommandBody(command, "deadline ");
                String description = parseTextBefore(commandBody, " /by ");
                String by = parseTextAfter(commandBody, " /by ");
                if (!description.isEmpty() && !by.isEmpty()) {
                    try {
                        addTask(tasks, new Deadline(description, by), ui);
                        saveTasks(storage, tasks, savingEnabled, ui);
                    } catch (IllegalArgumentException e) {
                        ui.showError(e.getMessage());
                    }
                } else {
                    ui.showError("Invalid deadline format. Use: deadline DESCRIPTION /by DEADLINE");
                }
                continue;
            } else if (command.equals("event") || command.startsWith("event ")) {
                String commandBody = parseCommandBody(command, "event ");
                String description = parseTextBefore(commandBody, " /from ");
                String fromAndTo = parseTextAfter(commandBody, " /from ");
                String from = parseTextBefore(fromAndTo, " /to ");
                String to = parseTextAfter(fromAndTo, " /to ");
                if (!description.isEmpty() && !from.isEmpty() && !to.isEmpty()) {
                    try {
                        addTask(tasks, new Event(description, from, to), ui);
                        saveTasks(storage, tasks, savingEnabled, ui);
                    } catch (IllegalArgumentException e) {
                        ui.showError(e.getMessage());
                    }
                } else {
                    ui.showError("Invalid event format. Use: event DESCRIPTION /from START /to END");
                }
                continue;
            } else if (command.equals("on") || command.startsWith("on ")) {
                try {
                    LocalDate date = LocalDate.parse(parseCommandBody(command, "on "));
                    listTasksOn(tasks, date, ui);
                } catch (DateTimeParseException e) {
                    ui.showError("Invalid date. Use: on yyyy-MM-dd (e.g., on 2019-12-02).");
                }
                continue;
            } else if (command.equals("list")) {
                ui.showTaskListHeader();
                for (int i = 0; i < tasks.size(); i++) {
                    ui.showNumberedTask(i + 1, tasks.get(i));
                }
                continue;
            } else if (command.equals("delete") || command.startsWith("delete ")) {
                int taskNumber = parseTaskNumber(command);
                if (taskNumber == -1) {
                    ui.showError(INVALID_DELETE_MESSAGE);
                } else if (taskNumber < 1 || taskNumber > tasks.size()) {
                    ui.showError(INVALID_TASK_NUMBER_MESSAGE);
                } else {
                    Task removedTask = tasks.remove(taskNumber - 1);
                    ui.showTaskDeleted(removedTask, tasks.size());
                    saveTasks(storage, tasks, savingEnabled, ui);
                }
                continue;
            } else if (command.equals("mark") || command.startsWith("mark ")) {
                int taskNumber = parseTaskNumber(command);
                if (taskNumber == -1) {
                    ui.showError(INVALID_MARK_MESSAGE);
                } else if (taskNumber < 1 || taskNumber > tasks.size()) {
                    ui.showError(INVALID_TASK_NUMBER_MESSAGE);
                } else {
                    Task task = tasks.get(taskNumber - 1);
                    boolean wasDone = task.isDone();
                    task.markAsDone();
                    ui.showTaskMarked(task);
                    if (!wasDone) {
                        saveTasks(storage, tasks, savingEnabled, ui);
                    }
                }
                continue;
            } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                int taskNumber = parseTaskNumber(command);
                if (taskNumber == -1) {
                    ui.showError(INVALID_UNMARK_MESSAGE);
                } else if (taskNumber < 1 || taskNumber > tasks.size()) {
                    ui.showError(INVALID_TASK_NUMBER_MESSAGE);
                } else {
                    if (!tasks.get(taskNumber - 1).isDone()) {
                        ui.showAlreadyUnmarked();
                    } else {
                        tasks.get(taskNumber - 1).unmarkAsDone();
                        ui.showTaskUnmarked(tasks.get(taskNumber - 1));
                        saveTasks(storage, tasks, savingEnabled, ui);
                    }
                }
                continue;
            }

            ui.showError(UNKNOWN_COMMAND_MESSAGE);
        }
    }

    /** Shows matching dated tasks using their original list numbers, without modifying or saving the list. */
    private static void listTasksOn(List<Task> tasks, LocalDate date, Ui ui) {
        ui.showDateHeader(date);
        boolean found = false;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).occursOn(date)) {
                ui.showNumberedTask(i + 1, tasks.get(i));
                found = true;
            }
        }
        if (!found) {
            ui.showNoTasksOnDate();
        }
    }

    /** Reports storage failures without terminating the command loop or discarding session tasks. */
    private static void saveTasks(Storage storage, List<Task> tasks, boolean savingEnabled, Ui ui) {
        if (!savingEnabled) {
            ui.showSavingDisabled();
            return;
        }
        try {
            storage.save(tasks);
        } catch (IOException | SecurityException e) {
            ui.showSavingError();
        }
    }

    /** Returns the trimmed command body, including an empty body for a missing argument. */
    private static String parseCommandBody(String command, String prefix) {
        if (!command.startsWith(prefix)) {
            return "";
        }
        return command.substring(prefix.length()).trim();
    }

    /** Adds a task and displays the updated list size. */
    private static void addTask(List<Task> tasks, Task task, Ui ui) {
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
    }

    /** Returns text before the first delimiter, or an empty string when it is missing. */
    private static String parseTextBefore(String text, String delimiter) {
        int delimiterIndex = text.indexOf(delimiter);
        if (delimiterIndex == -1) {
            return "";
        }
        return text.substring(0, delimiterIndex).trim();
    }

    /** Returns text after the first delimiter, or an empty string when it is missing. */
    private static String parseTextAfter(String text, String delimiter) {
        int delimiterIndex = text.indexOf(delimiter);
        if (delimiterIndex == -1) {
            return "";
        }
        return text.substring(delimiterIndex + delimiter.length()).trim();
    }

    /** Parses the command's task number, returning -1 for a missing or nonnumeric argument. */
    private static int parseTaskNumber(String command) {
        int firstSpaceIndex = command.indexOf(' ');
        if (firstSpaceIndex == -1) {
            return -1;
        }
        String taskNumberText = command.substring(firstSpaceIndex + 1).trim();
        try {
            return Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
