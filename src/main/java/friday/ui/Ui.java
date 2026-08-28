package friday.ui;

import java.time.LocalDate;
import java.util.Scanner;

import friday.task.Task;
import friday.task.TaskDateTime;

/** Handles console input and presentation without parsing commands or changing tasks. */
public class Ui {
    private static final String GOOD_DAY_ART = """
            ________________________________
           |                                |
           |  Good day to you sir!          |
           |________________________________|
            """;

    private static final String THANKS_ART = """
            ________________________
           |                        |
           |  Thanks!               |
           |________________________|
            """;

    private static final String SEPARATOR = "____________________________________________________________";
    private static final String TASK_NUMBER_HINT = "Use the number shown here with mark/unmark.";
    private final Scanner scanner;

    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Checks for input so EOF ends the command loop without an exception. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Returns the next line unchanged; interpreting it is the caller's responsibility. */
    public String readCommand() {
        return scanner.nextLine();
    }

    public void showLine() {
        System.out.println(SEPARATOR);
    }

    /** Prints the welcome text; the caller can show loading errors before the closing separator. */
    public void showWelcome() {
        showLine();
        System.out.println("Hello! I'm Friday.");
        System.out.println("What can I do for you?");
    }

    public void showGoodbye() {
        System.out.println("Bye. Hope to see you again soon!");
        showLine();
    }

    public void showGreeting() {
        System.out.println(GOOD_DAY_ART);
    }

    public void showThanks() {
        System.out.println(THANKS_ART);
    }

    public void showHelp() {
        System.out.println("Sure. Here you go:");
        System.out.println("https://nus-cs2103-ay2627-s1.github.io/website/schedule/week2/project.html");
    }

    /** Displays a validation error without deciding which command or input caused it. */
    public void showError(String message) {
        System.out.println(message);
    }

    public void showTaskListHeader() {
        System.out.println("Here are the tasks in your list:");
        System.out.println(TASK_NUMBER_HINT);
    }

    /**
     * Prints the keyword-search heading and explains how to use the original task numbers.
     */
    public void showFindHeader() {
        System.out.println("Here are the matching tasks in your list:");
        System.out.println(TASK_NUMBER_HINT);
    }

    /**
     * Reports that no task description contains the search keyword.
     */
    public void showNoMatchingTasks() {
        System.out.println("No matching tasks found.");
    }

    public void showDateHeader(LocalDate date) {
        System.out.println("Here are the deadlines and events on " + TaskDateTime.format(date.atStartOfDay()) + ":");
        System.out.println(TASK_NUMBER_HINT);
    }

    /** Displays the supplied original list number, including for filtered results. */
    public void showNumberedTask(int taskNumber, Task task) {
        System.out.println(taskNumber + "." + task);
    }

    public void showNoTasksOnDate() {
        System.out.println("No deadlines or events on this date.");
    }

    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        showTask(task);
        showTaskCount(taskCount);
    }

    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:");
        showTask(task);
        showTaskCount(taskCount);
    }

    public void showTaskMarked(Task task) {
        System.out.println("Nice! I've marked this task as done:");
        showTask(task);
    }

    public void showTaskUnmarked(Task task) {
        System.out.println("OK, I've marked this task as not done yet:");
        showTask(task);
    }

    public void showAlreadyUnmarked() {
        System.out.println("This task is already not marked as done.");
    }

    public void showLoadingError() {
        System.out.println("Warning: I couldn't load data/friday.txt. "
                + "Check the file and restart; saving is disabled to protect existing data.");
    }

    public void showSavingDisabled() {
        System.out.println("Warning: This change is only in memory; saving is disabled until you fix the file "
                + "and restart.");
    }

    public void showSavingError() {
        System.out.println("Warning: I couldn't save data/friday.txt. "
                + "Your changes are only in memory; check the folder and file permissions.");
    }

    private void showTask(Task task) {
        System.out.println("  " + task);
    }

    /** Keeps singular and plural task-count feedback consistent for additions and deletions. */
    private void showTaskCount(int taskCount) {
        System.out.println("Now you have " + taskCount + " task" + (taskCount == 1 ? "" : "s")
                + " in the list.");
    }
}
