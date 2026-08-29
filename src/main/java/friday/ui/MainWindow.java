package friday.ui;

import friday.Friday;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/** Controller for the primary Friday chat window. */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    private Friday friday;

    /** Keeps the newest dialog visible as the conversation grows. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the command engine after FXML has created this controller. */
    public void setFriday(Friday friday) {
        this.friday = friday;
        dialogContainer.getChildren().add(DialogBox.getFridayDialog(formatForGui(friday.getWelcomeMessage())));
    }

    /** Sends a command to Friday and appends both the command and response to the chat history. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getFridayDialog(formatForGui(friday.getResponse(input))));
        userInput.clear();
        if (friday.hasExited()) {
            userInput.setDisable(true);
        }
    }

    /** Removes console-only separators while retaining the shared command messages. */
    private String formatForGui(String response) {
        return response.replaceAll("(?m)^_{10,}\\R?", "").strip();
    }
}
