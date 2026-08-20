import java.util.Scanner;

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
            } else if (command.startsWith("deadline ")) {
                String description = parseDeadlineDescription(command);
                String by = parseDeadlineBy(command);
                if (!description.isEmpty() && !by.isEmpty()) {
                    tasks[taskCount] = new Deadline(description, by);
                    taskCount++;
                    System.out.println("Got it. I've added this task:");
                    System.out.println("  " + tasks[taskCount - 1]);
                    System.out.println("Now you have " + taskCount + " task" + (taskCount == 1 ? "" : "s")
                            + " in the list.");
                }
                continue;
            } else if (command.startsWith("todo ")) {
                String description = parseCommandBody(command, "todo ");
                if (!description.isEmpty()) {
                    tasks[taskCount] = new ToDo(description);
                    taskCount++;
                    System.out.println("Got it. I've added this task:");
                    System.out.println("  " + tasks[taskCount - 1]);
                    System.out.println("Now you have " + taskCount + " task" + (taskCount == 1 ? "" : "s")
                            + " in the list.");
                }
                continue;
            } else if (command.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + "." + tasks[i]);
                }
                continue;
            } else if (command.startsWith("mark ")) {
                //used Codex 5.4-mini to make the Task class and compared it without using class approach
                int taskNumber = parseTaskNumber(command);
                if (taskNumber >= 1 && taskNumber <= taskCount) {
                    tasks[taskNumber - 1].markAsDone();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + tasks[taskNumber - 1]);
                }
                continue;
            } else if (command.startsWith("unmark ")) {
                int taskNumber = parseTaskNumber(command);
                if (taskNumber >= 1 && taskNumber <= taskCount) {
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

            if (taskCount < tasks.length) {
                tasks[taskCount] = new ToDo(command);
                taskCount++;
                System.out.println("Got it. I've added this task:");
                System.out.println("  " + tasks[taskCount - 1]);
                System.out.println("Now you have " + taskCount + " task" + (taskCount == 1 ? "" : "s")
                        + " in the list.");
            }
        }
    }

    private static String parseDescription(String command) {
        return parseCommandBody(command, "todo ");
    }

    private static String parseDeadlineDescription(String command) {
        String commandBody = parseCommandBody(command, "deadline ");
        int byIndex = commandBody.indexOf(" /by ");
        if (byIndex == -1) {
            return "";
        }
        return commandBody.substring(0, byIndex).trim();
    }

    private static String parseDeadlineBy(String command) {
        String commandBody = parseCommandBody(command, "deadline ");
        int byIndex = commandBody.indexOf(" /by ");
        if (byIndex == -1) {
            return "";
        }
        return commandBody.substring(byIndex + " /by ".length()).trim();
    }

    private static String parseCommandBody(String command, String prefix) {
        if (!command.startsWith(prefix)) {
            return "";
        }
        return command.substring(prefix.length()).trim();
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
