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

    public static void main(String[] args) {
        //used Codex-5.4-mini to help the for loop to display items added into the array
        String separator = "____________________________________________________________";
        Scanner scanner = new Scanner(System.in);
        String[] tasks = new String[100];
        int taskCount = 0;

        System.out.println(separator);
        System.out.println("Hello! I'm Friday.");
        System.out.println("What can I do for you?");
        System.out.println(separator);

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            System.out.println(separator);
            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(separator);
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
            } else if (command.equals("list")) {
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + ". " + tasks[i]);
                }
                continue;
            }

            if (taskCount < tasks.length) {
                tasks[taskCount] = command;
                taskCount++;
                System.out.println("added: " + command);
            }
        }
    }
}
