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
import javafx.scene.layout.StackPane;
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
    private Label currentArtistLabel, currentSongsLabel, lblSongTimeUntilEnd, lblSongTimeSinceStart;
    @FXML
    private ListView<Song> songsInPlaylistListView;
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
    private int currentSongIndex;
    private Playlist selectedPlaylist;
    private int playlistIndex = -1;

    @FXML
    public void initialize() {
        showAllSongs();
        showAllPlaylists();

        setupListeners();
        allSongsTableView.setPlaceholder(new Label("No songs found"));
        playlistsTableView.setPlaceholder(new Label("No playlists found"));
        songsInPlaylistListView.setPlaceholder(new Label("No songs in playlist"));

        playSong(model.getQueue().get(0));
        playPauseMusic();
        volumeControlSlider.setValue(volume * 100);
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
        playlistsTableView.getSelectionModel().select(playlistIndex);
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
        List<Song> queue = model.getQueue();
        currentSongIndex = currentSongIndex == queue.size() - 1 ? 0 : currentSongIndex + 1;
        playSong(queue.get(currentSongIndex));
    }

    public void rewindMouseUp(MouseEvent mouseEvent) {
        resetOpacity(mouseEvent);
        rewindMusic();
    }

    /**
     * If the song has been playing for more than 2 seconds, it will be rewinded to the beginning.
     * Otherwise, the previous song will be played.
     */
    private void rewindMusic() {
        if (mediaPlayer.getCurrentTime().toSeconds() > 2)
            mediaPlayer.seek(Duration.ZERO);
        else {
            List<Song> queue = model.getQueue();
            currentSongIndex = currentSongIndex == 0 ? queue.size() - 1 : currentSongIndex - 1;
            playSong(queue.get(currentSongIndex));
        }
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
                if (selectedPlaylist == null) {
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
                model.deletePlaylist(selectedPlaylist);
                selectedPlaylist = null;
                playlistIndex = -1;
                showAllPlaylists();
                songsInPlaylistListView.getItems().clear();
            } else {
                Song song = songsInPlaylistListView.getSelectionModel().getSelectedItem();
                int songIndex = songsInPlaylistListView.getSelectionModel().getSelectedIndex() + 1; // database indexes start from 1
                model.deleteSongInPlaylist(song, selectedPlaylist, songIndex);
                showSongsInPlaylist();
                showAllPlaylists();
            }
        }
    }

    /**
     * Called when the user clicks the "new" button under playlist section
     * Opens a new window for creating new playlist
     * @param ignoredActionEvent The action event that triggered this method
     * @throws IOException thrown when the fxml file is not found
     */
    public void playlistNewButtonAction(ActionEvent ignoredActionEvent) throws IOException {
        Object[] objects = openNewWindow("Add Playlist", "views/new-playlist-view.fxml", "images/playlist.png");
        FXMLLoader fxmlLoader = (FXMLLoader) objects[0];
        Window window = (Window) objects[1];
        window.setOnHiding(event -> showAllPlaylists());
        NewPlaylistViewController newPlaylistViewController = fxmlLoader.getController();
        newPlaylistViewController.setModel(model);
        playlistIndex = model.getAllPlaylists().size();
    }

    /**
     * Called when the user clicks the "edit" button under playlist section
     * Opens a new window for editing the playlist's name
     * @param ignoredActionEvent The action event that triggered this method
     * @throws IOException Thrown when the fxml file is not found
     */
    public void playlistEditButtonAction(ActionEvent ignoredActionEvent) throws IOException {
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

    public void filterOnKeyTyped(KeyEvent ignoredKeyEvent) {
        allSongsTableView.getItems().setAll(model.search(filterTextField.getText()));
        allSongsTableView.refresh();
    }

    /**
     * Called when the user clicks the "new" button under all songs section
     * It opens new window for adding new song
     * @param ignoredActionEvent The action event that triggered this method
     * @throws IOException thrown when the fxml file is not found
     */
    public void songNewButtonAction(ActionEvent ignoredActionEvent) throws IOException {
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
     * @param ignoredActionEvent The action event that triggered this method
     * @throws IOException Thrown when the fxml file is not found
     */
    public void songEditButtonAction(ActionEvent ignoredActionEvent) throws IOException {
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
            newSongViewController.setComboBoxItems();
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
        if (song == null || selectedPlaylist == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a song and a playlist").showAndWait();
        } else {
            model.moveSongToPlaylist(song, selectedPlaylist);
            showSongsInPlaylist();
            showAllPlaylists();
        }
    }

    public void playlistTableViewOnMouseUp(MouseEvent ignoredMouseEvent) {
        if (playlistsTableView.getSelectionModel().getSelectedItem() != null){
            selectedPlaylist = playlistsTableView.getSelectionModel().getSelectedItem();
            playlistIndex = playlistsTableView.getSelectionModel().getSelectedIndex();
            showSongsInPlaylist();
        }

    }

    private String humanReadableTime(double seconds) {
        double hours = seconds / 3600;
        double minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d:%02d", (int) hours, (int) minutes, (int) seconds);
    }

    private void showSongsInPlaylist(){
        songsInPlaylistListView.setItems(model.getSongsInPlaylist(selectedPlaylist));
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
    }



    private void setMediaPlayerBehavior(){
        // without this there can be error for unknown duration
        mediaPlayer.setOnReady(() -> {
            StackPane trackPane = (StackPane) songTimeSlider.lookup(".track");
            mediaPlayer.setVolume(volume);
            mediaPlayer.setOnEndOfMedia(this::forwardMusic);
            // when the song is changed, the progress bar is updated
            songTimeSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
            // when the song is changed, the song time label is updated
            lblSongTimeUntilEnd.setText(humanReadableTime(mediaPlayer.getTotalDuration().toSeconds()));
            //
            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                //lblSongTimeUntilEnd.setText(humanReadableTime(mediaPlayer.getTotalDuration().toSeconds() - newValue.toSeconds()));
                if (!isUserChangingSongTime) {
                    lblSongTimeSinceStart.setText(humanReadableTime(newValue.toSeconds()));
                    songTimeSlider.setValue(newValue.toSeconds());
                }
            });
            songTimeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                // if user is changing time change the current time label
                if (isUserChangingSongTime) {
                lblSongTimeSinceStart.setText(humanReadableTime(newValue.doubleValue()));
                }
                // changing color of the slider behind and in front of play-head
                int sliderValue = (int) ((newValue.doubleValue() / mediaPlayer.getTotalDuration().toSeconds()) * 100) + 1; // times 100 because percentage and +1 because it hides double to int conversion
                String style = String.format("-fx-background-color: linear-gradient(to right, #2D819D %d%%, #969696 %d%%);",
                    sliderValue, sliderValue);
                trackPane.setStyle(style);
            });
            // terrible way to achieve styling on startup but other means are giving me null pointer exception so TODO: fix this
            songTimeSlider.setValue(1);
            songTimeSlider.setValue(0);
        });
    }

    public void songTimeSliderMouseUp(MouseEvent ignoredMouseEvent) {
        mediaPlayer.seek(Duration.seconds(songTimeSlider.getValue()));
        isUserChangingSongTime = false;
    }

    public void songTimeSliderMouseDown(MouseEvent ignoredMouseEvent) {
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
                int index = songsInPlaylistListView.getSelectionModel().getSelectedIndex();
                if (songsInPlaylistListView.getSelectionModel().getSelectedItem().getId() != songsInPlaylistListView.getItems().get(index-1).getId()){
                    model.moveSongInPlaylist(song, playlist, true, index);
                    songsInPlaylistListView.setItems(model.getSongsInPlaylist(playlist));
                }
               songsInPlaylistListView.getSelectionModel().select(index-1);
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
                int index = songsInPlaylistListView.getSelectionModel().getSelectedIndex();
                if (songsInPlaylistListView.getSelectionModel().getSelectedItem().getId() != songsInPlaylistListView.getItems().get(index+1).getId()){
                    model.moveSongInPlaylist(song, playlist, false, index);
                    songsInPlaylistListView.setItems(model.getSongsInPlaylist(playlist));
                }
                songsInPlaylistListView.getSelectionModel().select(index+1);
            }
        }
    }

    public void moveSongDownMouseDown(MouseEvent mouseEvent){
        ImageViewMouseDown(mouseEvent);
    }

    public void allSongsTableViewMouseClicked(MouseEvent mouseEvent) {
        // TODO: Sometimes playing songs takes too long for no apparent reason - optional: fix this
        List<Song> queue = model.getQueue();
        if (mouseEvent.getClickCount() == 2 && (queue.get(currentSongIndex) != null)) {
            model.setQueue(model.getAllSongs());
            currentSongIndex = allSongsTableView.getSelectionModel().getSelectedIndex();
            playSong(model.getQueue().get(currentSongIndex));
        }
    }

    private void playSong(Song song) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            isPlaying = false;
        }
        try {
            mediaPlayer = new MediaPlayer(new Media(Paths.get(song.getPath()).toUri().toString()));
            currentSongsLabel.setText(song.getTitle());
            currentArtistLabel.setText(song.getArtist().getName());
            lblSongTimeUntilEnd.setText(humanReadableTime(mediaPlayer.getTotalDuration().toSeconds()));
            setMediaPlayerBehavior();
            playPauseMusic();
        } catch (Exception e) {
            if (e.toString().contains("The system cannot find the path specified")) {
                new Alert(Alert.AlertType.ERROR, "The file \"" + song.getTitle() + "\" could not be found").showAndWait();
            } else {
                new Alert(Alert.AlertType.ERROR, "An error occurred while trying to play " + song.getTitle()).showAndWait();
            }
            // making sure the playing is stopped
            isPlaying = true;
            playPauseMusic();
        }
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
            model.setQueue(model.getSongsInPlaylist(playlist));
            playSong(model.getQueue().get(currentSongIndex));
        }
    }
}
