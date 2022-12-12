package mytunes.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import mytunes.MyTunes;
import mytunes.be.Playlist;
import mytunes.be.Song;
import mytunes.gui.models.Model;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MainWindowController {
    @FXML
    private Label currentArtistLabel;
    @FXML
    private Label currentSongsLabel;
    @FXML
    private ListView<Song> songsInPlaylistListView;
    @FXML
    private Label lblSongTimeUntilEnd, lblSongTimeSinceStart;
    @FXML
    private Slider volumeControlSlider, songTimeSlider;
    @FXML
    private TextField filterTextField;
    @FXML
    private ImageView playPauseButton;
    @FXML
    private TableView<Song> allSongsTableView;
    @FXML
    private TableColumn<Song, String> titleColumn, artistColumn, genreColumn, durationColumn;
    @FXML
    private TableView<Playlist> playlistsTableView;
    @FXML
    private TableColumn<Playlist, String> playlistNameColumn, totalLengthColumn;

    private boolean isPlaying = false;
    private boolean isUserChangingSongTime = false;
    private final Model model = new Model();

    private MediaPlayer mediaPlayer;

    private double volume = 0.05;
    private int currentSongIndex = 0;
    private List<Song> queue = model.getAllSongs();


    @FXML
    public void initialize() {
        showAllSongs();
        showAllPlaylists();

        setupListeners();
        allSongsTableView.setPlaceholder(new Label("No songs found"));
        playlistsTableView.setPlaceholder(new Label("No playlists found"));
        songsInPlaylistListView.setPlaceholder(new Label("No songs in playlist"));
    }

    public void setupListeners(){
        // Listener for loading all songs when filter text field is put in focus
        filterTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                model.loadSongsToMemory();
            else
                model.removeSongsFromMemory();
        });
        // listener for updating the song time slider
        volumeControlSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            volume = newValue.doubleValue() / 100;
            mediaPlayer.setVolume(volume);
        });
    }

    /**
     * Responsible for updating the tableview of all songs
     */
    private void showAllSongs() {
        allSongsTableView.refresh();
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        artistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("durationAsAString")); // cals getDurationAsAString() method
        allSongsTableView.getItems().setAll(model.getAllSongs());
    }

    /**
     * Responsible for updating the tableview of all playlists
     */
    private void showAllPlaylists(){
        playlistsTableView.refresh();
        playlistNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        totalLengthColumn.setCellValueFactory(new PropertyValueFactory<>("totalLengthAsAString"));
        playlistsTableView.getItems().setAll(model.getAllPlaylists());
    }

    /**
     * This method is called when the user clicks the ImageView representing play/pause button.
     * @param mouseEvent The mouse event that triggered this method.
     */
    public void playPauseMouseUp(MouseEvent mouseEvent) {
        resetOpacity(mouseEvent);
        playPauseMusic();
    }



    public void forwardMouseUp(MouseEvent mouseEvent) {
        resetOpacity(mouseEvent);
        forwardMusic();
    }

    private void forwardMusic() {
        currentSongIndex = currentSongIndex == queue.size() - 1 ? 0 : currentSongIndex + 1;
        playSong(queue.get(currentSongIndex));
    }

    public void rewindMouseUp(MouseEvent mouseEvent) {
        resetOpacity(mouseEvent);
        rewindMusic();
    }

    private void rewindMusic() {
        currentSongIndex = currentSongIndex == 0 ? queue.size()-1 : currentSongIndex - 1;
        playSong(queue.get(currentSongIndex));
    }

    /**
     * Changes opacity of the music controls buttons to 0.5 when mouse is pressed down on them
     * @param mouseEvent The mouse event that triggered this method
     */
    public void ImageViewMouseDown(MouseEvent mouseEvent) {
        ImageView imageView = (ImageView) mouseEvent.getSource();
        imageView.setOpacity(0.5);
    }

    /**
     * Resets the opacity of the button to 1.0
     * Used when the mouse is released
     * @param mouseEvent The mouse event that triggered this method
     */
    private void resetOpacity(MouseEvent mouseEvent) {
        ImageView imageView = (ImageView) mouseEvent.getSource();
        imageView.setOpacity(1);
    }

    /**
     * Called when the user clicks one of delete buttons
     * @param actionEvent The action event that triggered this method
     */
    public void deleteButtonAction(ActionEvent actionEvent){
        Node source = (Node) actionEvent.getSource();

        String type = "";
        if (source.getId().equals("playlistDeleteButton"))
            type = "playlist";
        else if (source.getId().equals("songsDeleteButton"))
            type = "song";
        else if (source.getId().equals("songsInPlaylistDeleteButton"))
            type = "song in playlist";

        // check if the user has selected a playlist or a song
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Error");
        switch (type) {
            case "song" -> {
                if (allSongsTableView.getSelectionModel().getSelectedItem() == null) {
                    errorAlert.setHeaderText("No song selected");
                    errorAlert.setContentText("Please select a song to delete");
                    errorAlert.showAndWait();
                    return;
                }
            }
            case "playlist" -> {
                if (playlistsTableView.getSelectionModel().getSelectedItem() == null) {
                    errorAlert.setHeaderText("No playlist selected");
                    errorAlert.setContentText("Please select a playlist to delete");
                    errorAlert.showAndWait();
                    return;
                }
            }
            case "song in playlist" -> {
                if (songsInPlaylistListView.getSelectionModel().getSelectedItem() == null) {
                    errorAlert.setHeaderText("No song selected");
                    errorAlert.setContentText("Please select a song to delete");
                    errorAlert.showAndWait();
                    return;
                }
            }
        }

        // ask the user if they are sure they want to delete the selected playlist/song
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete " + type);
        alert.setContentText("Do you really wish to delete this " + type + " ?");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            if (type.equals("song")) {
                Song song = allSongsTableView.getSelectionModel().getSelectedItem();
                model.deleteSong(song);
                showAllSongs();
                showSongsInPlaylist();
            } else if (type.equals("playlist")){
                Playlist playlist = playlistsTableView.getSelectionModel().getSelectedItem();
                model.deletePlaylist(playlist);
                showAllPlaylists();
            } else {
                Playlist playlist = playlistsTableView.getSelectionModel().getSelectedItem();
                Song song = songsInPlaylistListView.getSelectionModel().getSelectedItem();
                int songIndex = songsInPlaylistListView.getSelectionModel().getSelectedIndex();
                model.deleteSongInPlaylist(song, playlist, songIndex);
                showSongsInPlaylist();
                showAllPlaylists();
            }
        }
    }

    /**
     * Called when the user clicks the "new" button under playlist section
     * Opens a new window for creating new playlist
     * @param actionEvent The action event that triggered this method
     * @throws IOException thrown when the fxml file is not found
     */
    public void playlistNewButtonAction(ActionEvent actionEvent) throws IOException {
        Object[] objects = openNewWindow("Add Playlist", "views/new-playlist-view.fxml", "images/playlist.png");
        FXMLLoader fxmlLoader = (FXMLLoader) objects[0];
        Window window = (Window) objects[1];
        window.setOnHiding(event -> showAllPlaylists());
        NewPlaylistViewController newPlaylistViewController = fxmlLoader.getController();
        newPlaylistViewController.setModel(model);
    }

    /**
     * Called when the user clicks the "edit" button under playlist section
     * Opens a new window for editing the playlist's name
     * @param actionEvent The action event that triggered this method
     * @throws IOException Thrown when the fxml file is not found
     */
    public void playlistEditButtonAction(ActionEvent actionEvent) throws IOException {
        Playlist playlist = playlistsTableView.getSelectionModel().getSelectedItem();
        if (playlist == null)
            new Alert(Alert.AlertType.ERROR, "Please select a playlist to edit").showAndWait();
        else {
            model.setPlaylistToEdit(playlist);
            Object[] objects = openNewWindow("Edit Playlist", "views/new-playlist-view.fxml", "images/playlist.png");
            FXMLLoader fxmlLoader = (FXMLLoader) objects[0];
            Window window = (Window) objects[1];
            window.setOnHiding(event -> showAllPlaylists());
            NewPlaylistViewController newPlaylistViewController = fxmlLoader.getController();
            newPlaylistViewController.setModel(model);
            newPlaylistViewController.setIsEditing();
        }
    }

    public void filterOnKeyTyped(KeyEvent keyEvent) {
        allSongsTableView.getItems().setAll(model.search(filterTextField.getText()));
        allSongsTableView.refresh();
    }

    /**
     * Called when the user clicks the "new" button under all songs section
     * It opens new window for adding new song
     * @param actionEvent The action event that triggered this method
     * @throws IOException thrown when the fxml file is not found
     */
    public void songNewButtonAction(ActionEvent actionEvent) throws IOException {
        Object[] objects = openNewWindow("Add song", "views/new-song-view.fxml", "images/record.png");
        FXMLLoader fxmlLoader = (FXMLLoader) objects[0];
        Window window = (Window) objects[1];
        window.setOnHiding(event -> showAllSongs());
        NewSongViewController newSongViewController = fxmlLoader.getController();
        newSongViewController.setModel(model);
        newSongViewController.setComboBoxItems();
    }

    /**
     * Called when the user clicks the "edit" button under song section
     * Opens a new window for editing the song data
     * @param actionEvent The action event that triggered this method
     * @throws IOException Thrown when the fxml file is not found
     */
    public void songEditButtonAction(ActionEvent actionEvent) throws IOException {
        Song selectedSong = allSongsTableView.getSelectionModel().getSelectedItem();
        if (selectedSong == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a song to edit").showAndWait();
        } else {
            model.setSongToEdit(selectedSong);
            Object[] objects  = openNewWindow("Edit song", "views/new-song-view.fxml", "images/record.png");
            FXMLLoader fxmlLoader = (FXMLLoader) objects[0];
            Window window = (Window) objects[1];
            window.setOnHiding(event -> showAllSongs());
            NewSongViewController newSongViewController = fxmlLoader.getController();
            newSongViewController.setModel(model);
            newSongViewController.setIsEditing();
        }
    }

    private Object[] openNewWindow(String title, String fxmlFile, String iconFile) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MyTunes.class.getResource(fxmlFile));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.getIcons().add(new Image(Objects.requireNonNull(MyTunes.class.getResourceAsStream(iconFile))));
        stage.setTitle(title);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.centerOnScreen();
        Window window = scene.getWindow();
        stage.show();
        return new Object[]{fxmlLoader, window};
    }

    public void moveSongToPlaylistMouseDown(MouseEvent mouseEvent) {
        ImageViewMouseDown(mouseEvent);
    }

    /**
     * Called after clicking an arrow for moving songs from all songs tableview to playlist listview
     * On releasing the mouse button, the selected song is moved into a selected playlist
     * Throws an alert, if the user hasn't selected a song or a playlist
     * @param mouseEvent The mouse event that triggered this method
     */
    public void moveSongToPlaylistMouseUp(MouseEvent mouseEvent) {
        resetOpacity(mouseEvent);
        Song song = allSongsTableView.getSelectionModel().getSelectedItem();
        Playlist playlist = playlistsTableView.getSelectionModel().getSelectedItem();
        if (song == null || playlist == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a song and a playlist").showAndWait();
        } else {
            model.moveSongToPlaylist(song, playlist);
            showSongsInPlaylist();
            showAllPlaylists();
        }
    }

    public void playlistTableViewOnMouseUp(MouseEvent mouseEvent) {
        showSongsInPlaylist();
    }

    private String humanReadableTime(double seconds) {
        double hours = seconds / 3600;
        double minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d:%02d", (int) hours, (int) minutes, (int) seconds);
    }

    private void showSongsInPlaylist(){
        songsInPlaylistListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Song song, boolean empty) {
                super.updateItem(song, empty);
                if (empty || song == null || song.getTitle() == null) {
                    setText(null);
                } else {
                    setText(song.getIndexInPlaylist() + ": " + song.getTitle());
                }
            }
        });
        Playlist selectedPlaylist = playlistsTableView.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null) {
            songsInPlaylistListView.setItems(model.getSongsInPlaylist(selectedPlaylist));
        }
    }

    private void setMediaPlayerBehavior(){
        // without this there can be error for unknown duration
        mediaPlayer.setOnReady(() -> {

        mediaPlayer.setVolume(volume);
        mediaPlayer.setOnEndOfMedia(this::forwardMusic);
        songTimeSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
        lblSongTimeUntilEnd.setText(humanReadableTime(mediaPlayer.getTotalDuration().toSeconds()));
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            //lblSongTimeUntilEnd.setText(humanReadableTime(mediaPlayer.getTotalDuration().toSeconds() - newValue.toSeconds()));
            if (!isUserChangingSongTime) {
                lblSongTimeSinceStart.setText(humanReadableTime(newValue.toSeconds()));
                songTimeSlider.setValue(newValue.toSeconds());
            }
        });
        songTimeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (isUserChangingSongTime) {
                lblSongTimeSinceStart.setText(humanReadableTime(newValue.doubleValue()));
            }
        });
        });
    }

    public void songTimeSliderMouseUp(MouseEvent mouseEvent) {
        mediaPlayer.seek(Duration.seconds(songTimeSlider.getValue()));
        isUserChangingSongTime = false;
    }

    public void songTimeSliderMouseDown(MouseEvent mouseEvent) {
        isUserChangingSongTime = true;
        lblSongTimeSinceStart.setText(humanReadableTime(songTimeSlider.getValue()));
    }

    /**
     * Called after clicking an arrow for moving songs up in a playlist listview
     * On releasing the mouse button, the selected song is moved up by an index
     * Throws an alert, if the user hasn't selected a song to move
     * Throws an alert, if the user tries to move the first song
     * @param mouseEvent The mouse event that triggered this method
     */
    public void moveSongUpMouseUp(MouseEvent mouseEvent){
        resetOpacity(mouseEvent);
        Playlist playlist = playlistsTableView.getSelectionModel().getSelectedItem();
        Song song = songsInPlaylistListView.getSelectionModel().getSelectedItem();
        if (song == null)
            new Alert(Alert.AlertType.ERROR, "Please select a song to move").showAndWait();
        else{
            if (songsInPlaylistListView.getSelectionModel().getSelectedIndex() == 0)
                new Alert(Alert.AlertType.ERROR, "Can't move this song up").showAndWait();
            else{
                model.moveSongInPlaylist(song, playlist, true, songsInPlaylistListView.getSelectionModel().getSelectedIndex());
                songsInPlaylistListView.setItems(model.getSongsInPlaylist(playlist));
            }
        }
    }

    public void moveSongUpMouseDown(MouseEvent mouseEvent){
        ImageViewMouseDown(mouseEvent);
    }

    /**
     * Called after clicking an arrow for moving songs down in a playlist listview
     * On releasing the mouse button, the selected song is moved up down an index
     * Throws an alert, if the user hasn't selected a song to move
     * Throws an alert, if the user tries to move the last song
     * @param mouseEvent The mouse event that triggered this method
     */
    public void moveSongDownMouseUp(MouseEvent mouseEvent){
        resetOpacity(mouseEvent);
        Playlist playlist = playlistsTableView.getSelectionModel().getSelectedItem();
        Song song = songsInPlaylistListView.getSelectionModel().getSelectedItem();
        if (song == null)
            new Alert(Alert.AlertType.ERROR, "Please select a song to move").showAndWait();
        else{
            if (songsInPlaylistListView.getSelectionModel().getSelectedIndex() == songsInPlaylistListView.getItems().size()-1)
                new Alert(Alert.AlertType.ERROR, "Can't move this song down").showAndWait();
            else{
                model.moveSongInPlaylist(song, playlist, false, songsInPlaylistListView.getSelectionModel().getSelectedIndex());
                songsInPlaylistListView.setItems(model.getSongsInPlaylist(playlist));
            }
        }
    }

    public void moveSongDownMouseDown(MouseEvent mouseEvent){
        ImageViewMouseDown(mouseEvent);
    }

    public void allSongsTableViewMouseClicked(MouseEvent mouseEvent) {
        queue = model.getAllSongs();
        currentSongIndex = allSongsTableView.getSelectionModel().getSelectedIndex();
        if (mouseEvent.getClickCount() == 2 && (queue.get(currentSongIndex) != null)) {
            playSong(queue.get(currentSongIndex));
        }
    }

    private void playSong(Song song) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            isPlaying = false;
        }
        mediaPlayer = new MediaPlayer(new Media(Paths.get(song.getPath()).toUri().toString()));
        currentSongsLabel.setText(song.getTitle());
        currentArtistLabel.setText(song.getArtist().getName());
        lblSongTimeUntilEnd.setText(humanReadableTime(mediaPlayer.getTotalDuration().toSeconds()));
        setMediaPlayerBehavior();
        playPauseMusic();

    }
    private void playPauseMusic() {
        if (isPlaying) {
            playPauseButton.setImage(new Image(Objects.requireNonNull(MyTunes.class.getResourceAsStream("images/play.png"))));
            isPlaying = false;
            mediaPlayer.pause();
        } else {
            playPauseButton.setImage(new Image(Objects.requireNonNull(MyTunes.class.getResourceAsStream("images/pause.png"))));
            isPlaying = true;
            mediaPlayer.play();
        }
    }

    public void songsInPlaylistListviewMouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            Playlist playlist = playlistsTableView.getSelectionModel().getSelectedItem();
            currentSongIndex = songsInPlaylistListView.getSelectionModel().getSelectedIndex();
            queue = model.getSongsInPlaylist(playlist);
            playSong(queue.get(currentSongIndex));
        }
    }
}
