package friday.ui;

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

    /** Shows Friday's initial welcome and keeps the newest dialog visible. */
    @FXML
    public void initialize() {
        dialogContainer.getChildren().add(DialogBox.getFridayDialog("Hello! I'm Friday.\nWhat can I do for you?"));
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Echoes input while the command-processing integration is added in the next increment. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getFridayDialog("I received your command. Command processing is being connected next."));
        userInput.clear();
    }
}
