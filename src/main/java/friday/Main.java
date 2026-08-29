package friday;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** JavaFX entry point that loads Friday's main chat window from FXML. */
public class Main extends Application {
    /** Creates and displays the primary Friday chat window. */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Friday");
        stage.setMinWidth(480);
        stage.setMinHeight(640);
        stage.setScene(scene);
        stage.show();
    }
}
