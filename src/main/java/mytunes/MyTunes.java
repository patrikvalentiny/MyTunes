package mytunes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import mytunes.dal.DAOTools;

import java.io.IOException;
import java.util.Objects;

public class MyTunes extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MyTunes.class.getResource("views/my-tunes-main-window.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.getIcons().add(new Image(Objects.requireNonNull(MyTunes.class.getResourceAsStream("images/spotify.png"))));
        scene.getStylesheets().add(Objects.requireNonNull(MyTunes.class.getResource("css/mainstyle.css")).toExternalForm());
        stage.setTitle("MyTunes");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    @Override
    public void stop() {
        System.out.println("Closing application");
        DAOTools.closeAllConnections();
    }
    public static void main(String[] args) {
        launch();
    }
}