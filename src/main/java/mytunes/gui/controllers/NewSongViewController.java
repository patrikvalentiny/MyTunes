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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mytunes.MyTunes;
import mytunes.be.Artist;
import mytunes.be.Genre;
import mytunes.be.Song;
import mytunes.gui.models.Model;

import java.io.File;
import java.io.IOException;

public class NewSongViewController {
    @FXML
    private TextField txtFieldTitle, txtFieldArtist, txtFieldFile, txtFieldDuration;
    @FXML
    private ComboBox<String> comboBoxGenre;

    private Model model = null;

    private boolean isEditing = false;

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
     * @param actionEvent The action event that triggered this method
     * @throws IOException Thrown when the fxml file is not found
     */
    public void btnGenreMoreAction(ActionEvent actionEvent) throws IOException {
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
     * @param actionEvent The action event that triggered this method
     */
    public void btnFileChooseAction(ActionEvent actionEvent) {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a song file");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null)
            txtFieldFile.setText(file.getAbsolutePath());
    }

    /**
     * Called when a user clicks the "Save" button while creating a new song
     * Saves the song in the database and updates the list view in main window
     *
     * @param actionEvent The action event that triggered this method
     */
    public void btnSaveAction(ActionEvent actionEvent) {
        // Cleaning up the input and setting variables
        //TODO: make this a separate method
        String title = txtFieldTitle.getText().trim();
        String filepath = txtFieldFile.getText().trim();
        String artist = txtFieldArtist.getText().trim();
        String genre = comboBoxGenre.getValue();
        int duration = validateDurationInput(txtFieldDuration.getText().trim());
        //TODO wrong duration input

        // Validating the input
        if (title.isEmpty() || filepath.isEmpty() || duration == -1) {
            if (title.isEmpty())
                txtFieldTitle.setPromptText("Field must not be empty!");
            if (filepath.isEmpty())
                txtFieldFile.setPromptText("Field must not be empty!");
            if (duration == -1) {
                txtFieldDuration.setText("");
                txtFieldDuration.promptTextProperty().setValue("Invalid input! Duration in format mm:ss");
            }
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

    /**
     * Checks, whether the user-input duration of the song is valid and converts it into seconds
     *@param input The duration of the song
     */
    private int validateDurationInput(String input) {
        //TODO: this can create a lot of false positives which will fail in parseInt
        String[] inputArray = input.split(":");
        int duration = -1;
        if (inputArray.length == 3){ // hh:mm:ss
            duration = Integer.parseInt(inputArray[0])*3600; // 3600 is seconds in an hour
            duration += Integer.parseInt(inputArray[1])*60; // 60 is seconds in a minute
            duration += Integer.parseInt(inputArray[2]);
        }
        else if (inputArray.length == 2){ // mm:ss
            duration = Integer.parseInt(inputArray[0])*60; // seconds in a minute
            duration += Integer.parseInt(inputArray[1]);
        } else if (inputArray.length == 1){ // ss
            duration = Integer.parseInt(inputArray[0]);
        }
        return duration;
    }

    public void setIsEditing() {
        isEditing = true;
        Song songToEdit = model.getSongToEdit();
        txtFieldTitle.setText(songToEdit.getTitle());
        txtFieldArtist.setText(songToEdit.getArtist().getName());
        comboBoxGenre.setValue(songToEdit.getGenre().getName());
        txtFieldFile.setText(songToEdit.getPath());
        txtFieldDuration.setText("" + songToEdit.getDuration());
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
}
