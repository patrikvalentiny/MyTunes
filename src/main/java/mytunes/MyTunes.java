package mytunes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import mytunes.be.Genre;
import mytunes.be.Playlist;
import mytunes.dal.dao.GenreDAO;
import mytunes.dal.dao.PlaylistDAO;
import mytunes.dal.dao.SongDAO;

import java.io.IOException;
import java.util.List;
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
        test();
    }

    public static void main(String[] args) {
        launch();
    }

    public void test(){
        GenreDAO genreDAO = new GenreDAO();
        genreDAO.createGenre(new Genre("Rock"));
        genreDAO.createGenre(new Genre("Metal"));
    }
}