package friday;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import friday.parser.Parser;
import friday.storage.Storage;
import friday.task.Task;
import friday.task.TaskList;
import friday.ui.Ui;

/** Friday's command engine. It owns task state and storage while exposing text responses for any UI. */
public class Friday {
    private static final String INVALID_TASK_NUMBER_MESSAGE = "Sir, The task number is invalid.";
    private static final Path DATA_FILE = Path.of("data", "friday.txt");

    private final Storage storage;
    private final Ui ui;
    private TaskList tasks;
    private boolean isSavingEnabled;
    private boolean hasLoadingError;
    private boolean hasExited;

    /** Loads Friday's default task data file. */
    public Friday() {
        this(DATA_FILE);
    }

    /**
     * Loads tasks from the supplied path, keeping the session usable if saved data cannot be read.
     *
     * @param dataFile location of Friday's persistent task file.
     */
    public Friday(Path dataFile) {
        storage = new Storage(dataFile);
        ui = new Ui();
        tasks = new TaskList();
        isSavingEnabled = true;
        try {
            tasks = new TaskList(storage.load());
        } catch (IOException | SecurityException e) {
            isSavingEnabled = false;
            hasLoadingError = true;
        }
    }

    /** Runs the console interface while reusing the response API used by the JavaFX GUI. */
    public static void main(String[] args) {
        Friday friday = new Friday();
        System.out.print(friday.getWelcomeMessage());
        while (friday.ui.hasNextCommand() && !friday.hasExited) {
            System.out.print(friday.getResponse(friday.ui.readCommand()));
        }
    }

    /**
     * Returns Friday's initial welcome and any storage warning as display-ready text.
     *
     * @return the welcome response for a user interface.
     */
    public synchronized String getWelcomeMessage() {
        return captureOutput(() -> {
            ui.showWelcome();
            if (hasLoadingError) {
                ui.showLoadingError();
            }
            ui.showLine();
        });
    }

    /**
     * Processes one user command and returns all resulting output for a user interface to display.
     *
     * @param command unmodified command text from the user.
     * @return Friday's response to the command.
     */
    public synchronized String getResponse(String command) {
        return captureOutput(() -> processCommand(command));
    }

    /**
     * Returns whether the user has entered {@code bye}; UIs can disable further input if desired.
     *
     * @return true after Friday has processed a bye command.
     */
    public boolean hasExited() {
        return hasExited;
    }

    /** Executes a command through the existing parser, task list, storage, and presentation classes. */
    private void processCommand(String command) {
        ui.showLine();
        try {
            boolean hasChanged = false;
            switch (Parser.parseCommandType(command)) {
            case BYE -> {
                ui.showGoodbye();
                hasExited = true;
            }
            case HELLO -> ui.showGreeting();
            case THANKS -> ui.showThanks();
            case HELP -> ui.showHelp();
            case TODO, DEADLINE, EVENT -> {
                Task task = Parser.parseTask(command);
                tasks.add(task);
                ui.showTaskAdded(task, tasks.size());
                hasChanged = true;
            }
            case ON -> listTasksOn(Parser.parseDate(command));
            case FIND -> listMatchingTasks(Parser.parseFindKeyword(command));
            case LIST -> showTaskList();
            case DELETE -> {
                int taskNumber = requireExistingTaskNumber(command);
                Task removedTask = tasks.delete(taskNumber);
                ui.showTaskDeleted(removedTask, tasks.size());
                hasChanged = true;
            }
            case MARK -> {
                int taskNumber = requireExistingTaskNumber(command);
                hasChanged = tasks.mark(taskNumber);
                ui.showTaskMarked(tasks.get(taskNumber));
            }
            case UNMARK -> {
                int taskNumber = requireExistingTaskNumber(command);
                hasChanged = tasks.unmark(taskNumber);
                if (hasChanged) {
                    ui.showTaskUnmarked(tasks.get(taskNumber));
                } else {
                    ui.showAlreadyUnmarked();
                }
            }
            }
            if (hasChanged) {
                saveTasks();
            }
        } catch (IllegalArgumentException e) {
            ui.showError(e.getMessage());
        }
    }

    /** Captures the existing console UI output so it can be rendered in a GUI without duplicating messages. */
    private String captureOutput(Runnable action) {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        try (PrintStream responseOutput = new PrintStream(capturedOutput, true, StandardCharsets.UTF_8)) {
            System.setOut(responseOutput);
            action.run();
        } finally {
            System.setOut(originalOutput);
        }
        return capturedOutput.toString(StandardCharsets.UTF_8);
    }

    /** Checks parsed task numbers against the current list; the parser does not depend on list state. */
    private int requireExistingTaskNumber(String command) {
        int taskNumber = Parser.parseTaskNumber(command);
        if (!tasks.isValidTaskNumber(taskNumber)) {
            throw new IllegalArgumentException(INVALID_TASK_NUMBER_MESSAGE);
        }
        return taskNumber;
    }

    /** Shows all tasks with their original numbers. */
    private void showTaskList() {
        ui.showTaskListHeader();
        for (int taskNumber = 1; taskNumber <= tasks.size(); taskNumber++) {
            ui.showNumberedTask(taskNumber, tasks.get(taskNumber));
        }
    }

    /** Shows dated tasks using their original list numbers without modifying or saving the list. */
    private void listTasksOn(LocalDate date) {
        ui.showDateHeader(date);
        List<Integer> matches = tasks.findTaskNumbersOn(date);
        if (matches.isEmpty()) {
            ui.showNoTasksOnDate();
        }
        for (int taskNumber : matches) {
            ui.showNumberedTask(taskNumber, tasks.get(taskNumber));
        }
    }

    /** Shows keyword matches using original list numbers without modifying or saving the list. */
    private void listMatchingTasks(String keyword) {
        ui.showFindHeader();
        List<Integer> matches = tasks.findTaskNumbersContaining(keyword);
        if (matches.isEmpty()) {
            ui.showNoMatchingTasks();
        }
        for (int taskNumber : matches) {
            ui.showNumberedTask(taskNumber, tasks.get(taskNumber));
        }
    }

    /** Reports storage failures without terminating the command session or discarding in-memory tasks. */
    private void saveTasks() {
        if (!isSavingEnabled) {
            ui.showSavingDisabled();
            return;
        }
        try {
            storage.save(tasks.toList());
        } catch (IOException | SecurityException e) {
            ui.showSavingError();
        }
    }
}
