package friday.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import friday.Response;

/** Displays compact user commands and wider assistant replies with explicit severity labels. */
public class DialogBox extends HBox {
    private static final double AVATAR_SIZE = 36;
    private static final Image USER_AVATAR = loadAvatar("/images/user-hero.png");
    private static final Image FRIDAY_AVATAR = loadAvatar("/images/friday-core.png");

    @FXML
    private Label dialog;
    @FXML
    private Label speaker;
    @FXML
    private ImageView displayPicture;
    @FXML
    private VBox messagePanel;

    private DialogBox(String text, boolean isFriday, Response.Severity severity) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog-box layout.", e);
        }
        dialog.setText(text);
        displayPicture.setImage(isFriday ? FRIDAY_AVATAR : USER_AVATAR);
        if (!isFriday) {
            // Focus the supplied full-body artwork on the mask and upper torso.
            displayPicture.setViewport(new Rectangle2D(USER_AVATAR.getWidth() * 0.34,
                    USER_AVATAR.getHeight() * 0.24, USER_AVATAR.getWidth() * 0.34,
                    USER_AVATAR.getWidth() * 0.34));
        }
        Rectangle clip = new Rectangle(AVATAR_SIZE, AVATAR_SIZE);
        clip.setArcWidth(12);
        clip.setArcHeight(12);
        displayPicture.setClip(clip);
        if (isFriday) {
            speaker.setText(switch (severity) {
                case NORMAL -> "FRIDAY";
                case ERROR -> "FRIDAY · Command error";
                case WARNING -> "FRIDAY · Warning";
            });
            getStyleClass().add(switch (severity) {
                case NORMAL -> "assistant";
                case ERROR -> "error";
                case WARNING -> "warning";
            });
        } else {
            getStyleClass().add("user");
            speaker.setText("YOU");
            getChildren().setAll(messagePanel, displayPicture);
            setAlignment(Pos.TOP_RIGHT);
            HBox.setHgrow(messagePanel, Priority.NEVER);
            // Leave some visual asymmetry without sacrificing reply width on small windows.
            messagePanel.maxWidthProperty().bind(widthProperty().subtract(AVATAR_SIZE + 8).multiply(0.85));
        }
    }

    /** Creates a right-aligned command bubble. */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, false, Response.Severity.NORMAL);
    }

    /** Creates a normal assistant panel for existing callers. */
    public static DialogBox getFridayDialog(String text) {
        return new DialogBox(text, true, Response.Severity.NORMAL);
    }

    /** Creates an assistant panel using explicit response severity. */
    public static DialogBox getFridayDialog(Response response) {
        String text = response.text().replaceAll("(?m)^_{10,}\\R?", "").strip();
        return new DialogBox(text, true, response.severity());
    }

    /** Loads each portrait once rather than decoding it for every message. */
    private static Image loadAvatar(String path) {
        return new Image(DialogBox.class.getResourceAsStream(path), 288, 288, true, true);
    }
}
