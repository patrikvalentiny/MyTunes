package mytunes.gui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mytunes.MyTunes;
import mytunes.be.Artist;
import mytunes.be.Genre;
import mytunes.be.Song;
import mytunes.gui.models.Model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class NewSongViewController {
    @FXML
    private TextField txtFieldTitle, txtFieldArtist, txtFieldFile, txtFieldDuration;
    @FXML
    private ComboBox<String> comboBoxGenre;

    private Model model = null;
    private boolean isEditing = false;
    private int duration;

    public void setModel(Model model) {
        this.model = model;
    }

    @FXML
    public void initialize() {
        isEditing = false;
    }

    /**
     * Called when a user clicks "More" button while creating a new song
     * Enables a user to create and add another genre to the combobox
     *
     * @param ignoredActionEvent The action event that triggered this method
     * @throws IOException Thrown when the fxml file is not found
     */
    public void btnGenreMoreAction(ActionEvent ignoredActionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MyTunes.class.getResource("views/new-genre-view.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Add genre");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.centerOnScreen();
        NewGenreViewController newGenreViewController = fxmlLoader.getController();
        newGenreViewController.setModel(model);
        scene.getWindow().setOnHiding(event -> setComboBoxItems());
        stage.show();
    }

    /**
     * Called when a user clicks the "Choose" button while creating a new song
     * Opens a window which enables user to choose a file path from their documents
     *
     * @param ignoredActionEvent The action event that triggered this method
     */
    public void btnFileChooseAction(ActionEvent ignoredActionEvent) {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        File file;
        fileChooser.setTitle("Choose a song file");
        try {
            if (txtFieldFile.getText() != null) {
                File previousFile = new File(Paths.get(txtFieldFile.getText()).toUri());
                fileChooser.setInitialDirectory(new File(previousFile.getParentFile().getAbsolutePath()));
                fileChooser.setInitialFileName(previousFile.getName());
            }
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("MP3 files", "*.mp3"));
            file = fileChooser.showOpenDialog(stage);
        } catch (Exception e){
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Music"));
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("MP3 files", "*.mp3"));
            file = fileChooser.showOpenDialog(stage);
        }
        if (file != null){
            txtFieldFile.setText(file.getAbsolutePath());

            //waits until media is ready, fills out available data for the user
            Media media = new Media(file.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setOnReady(() -> {
                double d = media.getDuration().toSeconds();
                duration = (int) d;
                txtFieldDuration.setText(humanReadableTime(duration));
                txtFieldTitle.setText((String) media.getMetadata().get("title"));
                txtFieldArtist.setText((String) media.getMetadata().get("artist"));
                comboBoxGenre.setValue((String) media.getMetadata().get("genre"));
            });
        }
    }

    /**
     * Called when a user clicks the "Save" button while creating a new song
     * Saves the song in the database and updates the list view in main window
     *
     * @param actionEvent The action event that triggered this method
     */
    public void btnSaveAction(ActionEvent actionEvent) {
        // Cleaning up the input and setting variables
        String title = txtFieldTitle.getText().trim();
        String filepath = txtFieldFile.getText().trim();
        String artist = txtFieldArtist.getText().trim();
        String genre = comboBoxGenre.getValue();

        // Validating the input
        if (title.isEmpty() || filepath.isEmpty()) {
            if (title.isEmpty())
                txtFieldTitle.setPromptText("Field must not be empty!");
            if (filepath.isEmpty())
                txtFieldFile.setPromptText("Field must not be empty!");
        } else {
            if (isEditing)
                model.updateSong(new Song(title, new Artist(artist), new Genre(genre), filepath, duration));
            else
                model.createSong(new Song(title, new Artist(artist), new Genre(genre), filepath, duration));

            Node node = (Node) actionEvent.getSource();
            node.getScene().getWindow().hide();
        }
    }

    /**
     * Cancels creating a new song
     *
     * @param actionEvent The action event that triggered this method
     */
    public void btnCancelAction(ActionEvent actionEvent) {
        Node node = (Node) actionEvent.getSource();
        node.getScene().getWindow().hide();
    }

    public void setIsEditing() {
        isEditing = true;
        Song songToEdit = model.getSongToEdit();
        txtFieldTitle.setText(songToEdit.getTitle());
        txtFieldArtist.setText(songToEdit.getArtist().getName());
        comboBoxGenre.setValue(songToEdit.getGenre().getName());
        txtFieldFile.setText(songToEdit.getPath());
        txtFieldDuration.setText(humanReadableTime(songToEdit.getDuration()));
    }

    public void setComboBoxItems(){
        ObservableList<Genre> genres = FXCollections.observableArrayList();
        genres.clear();
        comboBoxGenre.getItems().removeAll(comboBoxGenre.getItems());
        genres.addAll(model.getAllGenres());
        for (Genre g : genres){
            comboBoxGenre.getItems().add(g.getName());
        }
    }

    private String humanReadableTime(double seconds) {
        double hours = seconds / 3600;
        double minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d:%02d", (int) hours, (int) minutes, (int) seconds);
    }
}
