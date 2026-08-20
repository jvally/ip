import java.util.Scanner;

/**
 * Friday chatbot entry point.
 * Uses inheritance for shared Task behavior and polymorphism to store multiple task types in one Task[].
 */
public class Friday {
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
    private static final String UNKNOWN_COMMAND_MESSAGE = "Sir, I don't know what you are saying :-(";
    private static final String EMPTY_TODO_MESSAGE = "Sir, description of a todo cannot be empty.";
    private static final String INVALID_MARK_MESSAGE = "Sir, Invalid mark format. Use: mark TASK_NUMBER";
    private static final String INVALID_UNMARK_MESSAGE = "Sir, Invalid unmark format. Use: unmark TASK_NUMBER";
    private static final String INVALID_DELETE_MESSAGE = "Sir, Invalid delete format. Use: delete TASK_NUMBER";
    private static final String INVALID_TASK_NUMBER_MESSAGE = "Sir, The task number is invalid.";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[100];
        int taskCount = 0;

        System.out.println(SEPARATOR);
        System.out.println("Hello! I'm Friday.");
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            System.out.println(SEPARATOR);
            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(SEPARATOR);
                break;
            } else if (command.equals("hello")) {
                System.out.println(GOOD_DAY_ART);
                continue;
            } else if (command.equals("thanks")) {
                System.out.println(THANKS_ART);
                continue;
            } else if (command.equals("help")) {
                System.out.println("Sure. Here you go:");
                System.out.println("https://nus-cs2103-ay2627-s1.github.io/website/schedule/week2/project.html");
                continue;
            } else if (command.equals("todo") || command.startsWith("todo ")) {
                String description = parseCommandBody(command, "todo ");
                if (!description.isEmpty()) {
                    taskCount = addTask(tasks, taskCount, new ToDo(description));
                } else {
                    System.out.println(EMPTY_TODO_MESSAGE);
                }
                continue;
            } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                String commandBody = parseCommandBody(command, "deadline ");
                String description = parseTextBefore(commandBody, " /by ");
                String by = parseTextAfter(commandBody, " /by ");
                if (!description.isEmpty() && !by.isEmpty()) {
                    taskCount = addTask(tasks, taskCount, new Deadline(description, by));
                } else {
                    System.out.println("Invalid deadline format. Use: deadline DESCRIPTION /by DEADLINE");
                }
                continue;
            } else if (command.equals("event") || command.startsWith("event ")) {
                String commandBody = parseCommandBody(command, "event ");
                String description = parseTextBefore(commandBody, " /from ");
                String fromAndTo = parseTextAfter(commandBody, " /from ");
                String from = parseTextBefore(fromAndTo, " /to ");
                String to = parseTextAfter(fromAndTo, " /to ");
                if (!description.isEmpty() && !from.isEmpty() && !to.isEmpty()) {
                    taskCount = addTask(tasks, taskCount, new Event(description, from, to));
                } else {
                    System.out.println("Invalid event format. Use: event DESCRIPTION /from START /to END");
                }
                continue;
            } else if (command.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                System.out.println("Use the number shown here with mark/unmark.");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + "." + tasks[i]);
                }
                continue;
            } else if (command.equals("delete") || command.startsWith("delete ")) {
                int taskNumber = parseTaskNumber(command);
                if (taskNumber == -1) {
                    System.out.println(INVALID_DELETE_MESSAGE);
                } else if (taskNumber < 1 || taskNumber > taskCount) {
                    System.out.println(INVALID_TASK_NUMBER_MESSAGE);
                } else {
                    Task removedTask = deleteTask(tasks, taskCount, taskNumber);
                    taskCount--;
                    System.out.println("Noted. I've removed this task:");
                    System.out.println("  " + removedTask);
                    System.out.println("Now you have " + taskCount + " task" + (taskCount == 1 ? "" : "s")
                            + " in the list.");
                }
                continue;
            } else if (command.equals("mark") || command.startsWith("mark ")) {
                int taskNumber = parseTaskNumber(command);
                if (taskNumber == -1) {
                    System.out.println(INVALID_MARK_MESSAGE);
                } else if (taskNumber < 1 || taskNumber > taskCount) {
                    System.out.println(INVALID_TASK_NUMBER_MESSAGE);
                } else {
                    tasks[taskNumber - 1].markAsDone();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + tasks[taskNumber - 1]);
                }
                continue;
            } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                int taskNumber = parseTaskNumber(command);
                if (taskNumber == -1) {
                    System.out.println(INVALID_UNMARK_MESSAGE);
                } else if (taskNumber < 1 || taskNumber > taskCount) {
                    System.out.println(INVALID_TASK_NUMBER_MESSAGE);
                } else {
                    if (!tasks[taskNumber - 1].isDone()) {
                        System.out.println("This task is already not marked as done.");
                    } else {
                        tasks[taskNumber - 1].unmarkAsDone();
                        System.out.println("OK, I've marked this task as not done yet:");
                        System.out.println("  " + tasks[taskNumber - 1]);
                    }
                }
                continue;
            }

            System.out.println(UNKNOWN_COMMAND_MESSAGE);
        }
    }

    private static String parseCommandBody(String command, String prefix) {
        if (!command.startsWith(prefix)) {
            return "";
        }
        return command.substring(prefix.length()).trim();
    }

    private static int addTask(Task[] tasks, int taskCount, Task task) {
        tasks[taskCount] = task;
        taskCount++;
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " task" + (taskCount == 1 ? "" : "s")
                + " in the list.");
        return taskCount;
    }

    private static Task deleteTask(Task[] tasks, int taskCount, int taskNumber) {
        Task removedTask = tasks[taskNumber - 1];
        for (int i = taskNumber - 1; i < taskCount - 1; i++) {
            tasks[i] = tasks[i + 1];
        }
        tasks[taskCount - 1] = null;
        return removedTask;
    }

    private static String parseTextBefore(String text, String delimiter) {
        int delimiterIndex = text.indexOf(delimiter);
        if (delimiterIndex == -1) {
            return "";
        }
        return text.substring(0, delimiterIndex).trim();
    }

    private static String parseTextAfter(String text, String delimiter) {
        int delimiterIndex = text.indexOf(delimiter);
        if (delimiterIndex == -1) {
            return "";
        }
        return text.substring(delimiterIndex + delimiter.length()).trim();
    }

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
