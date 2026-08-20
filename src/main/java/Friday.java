import java.util.Scanner;

public class Friday {
    //used ChatGPT 5.4-mini to help me do the ascii art
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
        String separator = "____________________________________________________________";
        Scanner scanner = new Scanner(System.in);

        System.out.println(separator);
        System.out.println("Wassup! I'm Friday, your personalized academic weapon.");
        System.out.println("What do you need today?");
        System.out.println(separator);

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            System.out.println(separator);
            if (command.equals("bye")) {
                System.out.println("Bye. I hope I DON'T see you again soon:)");
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
            }

            System.out.println(command);
        }
    }
}
