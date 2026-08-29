package friday.ui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/** A reusable chat message consisting of speaker text and an avatar. */
public class DialogBox extends HBox {
    private static final Image AVATAR_SPRITE = new Image(DialogBox.class.getResourceAsStream("/images/avatars.png"));
    private static final double AVATAR_SIZE = 56;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, boolean isFriday) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog-box layout.", e);
        }
        dialog.setText(text);
        configureAvatar(isFriday);
    }

    /** Creates a right-aligned dialog box for a user command. */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, false);
    }

    /** Creates a left-aligned dialog box for Friday's response. */
    public static DialogBox getFridayDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, true);
        dialogBox.flip();
        return dialogBox;
    }

    /** Selects one avatar from the two-character sprite and scales it for a chat row. */
    private void configureAvatar(boolean isFriday) {
        double halfWidth = AVATAR_SPRITE.getWidth() / 2;
        displayPicture.setImage(AVATAR_SPRITE);
        displayPicture.setViewport(new Rectangle2D(isFriday ? halfWidth : 0, 0,
                halfWidth, AVATAR_SPRITE.getHeight()));
        displayPicture.setFitHeight(AVATAR_SIZE);
        displayPicture.setFitWidth(AVATAR_SIZE);
        displayPicture.setPreserveRatio(true);
    }

    /** Places Friday's avatar on the left to distinguish replies from user messages. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
