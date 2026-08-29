package friday;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests that the UI-neutral Friday response API preserves core chatbot behavior. */
class FridayTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_taskCommandsSaveAndListTask() {
        Friday friday = new Friday(temporaryDirectory.resolve("friday.txt"));

        String addResponse = friday.getResponse("todo read GUI tutorial");
        String listResponse = friday.getResponse("list");

        assertTrue(addResponse.contains("I've added this task"));
        assertTrue(listResponse.contains("1.[T][ ] read GUI tutorial"));
        assertTrue(temporaryDirectory.resolve("friday.txt").toFile().isFile());
    }

    @Test
    void getResponse_invalidCommandDoesNotPreventLaterCommands() {
        Friday friday = new Friday(temporaryDirectory.resolve("friday.txt"));

        String errorResponse = friday.getResponse("unknown");
        String addResponse = friday.getResponse("todo recover after error");

        assertTrue(errorResponse.contains("I don't know what you are saying"));
        assertTrue(addResponse.contains("recover after error"));
        assertFalse(friday.hasExited());
    }

    @Test
    void getResponse_byeMarksSessionAsExited() {
        Friday friday = new Friday(temporaryDirectory.resolve("friday.txt"));

        String response = friday.getResponse("bye");

        assertTrue(response.contains("Bye. Hope to see you again soon!"));
        assertTrue(friday.hasExited());
    }
}
