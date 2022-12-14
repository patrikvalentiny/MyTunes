package mytunes.gui.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mytunes.be.Genre;
import mytunes.be.Playlist;
import mytunes.be.Song;
import mytunes.bll.LogicInterface;
import mytunes.bll.LogicManager;

import java.util.List;

public class Model {
    private final ObservableList<Genre> genres;
    private final ObservableList<Playlist> playlists;
    private final ObservableList<Song> allSongs, songsInPlaylist;
    private Song songToEdit;
    private Playlist playlistToEdit;
    private final LogicInterface bll = new LogicManager();

    private List<Song> queue;

    public Model(){
        genres = FXCollections.observableArrayList();
        playlists = FXCollections.observableArrayList();
        allSongs = FXCollections.observableArrayList();
        songsInPlaylist = FXCollections.observableArrayList();
        queue = getAllSongs();
    }

    public List<Song> getQueue() {
        return queue;
    }

    public void setQueue(List<Song> queue) {
        this.queue = queue;
    }

    public Song getSongToEdit() {
        return songToEdit;
    }

    public void setSongToEdit(Song songToEdit) {
        this.songToEdit = songToEdit;
    }

    public void createSong(Song song){
        bll.createSong(song);
        loadAllSongs();
    }

    public void deleteSong(Song song){
        bll.deleteSong(song);
        loadAllSongs();
    }

    public void loadAllSongs(){
        allSongs.clear();
        allSongs.addAll(bll.getAllSongs());
    }

    public ObservableList<Song> getAllSongs() {
        loadAllSongs();
        return allSongs;
    }

    public void updateSong(Song song){
        bll.updateSong(new Song(songToEdit.getId(), song.getTitle(), song.getArtist(), song.getGenre(), song.getPath(), song.getDuration()));
    }

    public void createPlaylist(Playlist playlist){
        bll.createPlaylist(playlist);
        loadAllPlaylists();
    }

    public void deletePlaylist(Playlist playlist) {
        bll.deletePlaylist(playlist);
        loadAllPlaylists();
    }

    public void updatePlaylist(Playlist playlist){
        bll.updatePlaylist(new Playlist(playlistToEdit.getId(), playlist.getName(), playlist.getTotalLength()));
    }

    private void loadAllPlaylists() {
        playlists.clear();
        playlists.addAll(bll.getAllPlaylists());
    }

    public ObservableList<Playlist> getAllPlaylists(){
        loadAllPlaylists();
        return playlists;
    }

    public Playlist getPlaylistToEdit() {
        return playlistToEdit;
    }

    public void setPlaylistToEdit(Playlist playlist){
        this.playlistToEdit = playlist;
    }

    public void createGenre(String name) {
        bll.createGenre(new Genre(name));
        loadAllGenres();
    }

    private void loadAllGenres(){
        genres.clear();
        genres.addAll(bll.getAllGenres());
    }

    public List<Genre> getAllGenres(){
        loadAllGenres();
        return genres;
    }

    public List<Song> search(String query){
        return bll.filterSongs(query);
    }

    public void loadSongsToMemory(){
        bll.loadSongsToMemory();
    }

    public void removeSongsFromMemory(){
        bll.removeSongsFromMemory();
    }

    public void moveSongToPlaylist(Song song, Playlist playlist) {
        bll.addSongToPlaylist(song, playlist);
    }

    public ObservableList<Song> getSongsInPlaylist(Playlist playlist){
        songsInPlaylist.clear();
        songsInPlaylist.addAll(bll.getSongsInPlaylist(playlist));
        return songsInPlaylist;
    }

    public void moveSongInPlaylist(Song song, Playlist playlist, Boolean moveUp, int songIndex) {
        bll.moveSongInPlaylist(song, playlist, moveUp, songIndex + 1); // +1 because the index is 0-based
        songsInPlaylist.clear();
        songsInPlaylist.addAll(bll.getSongsInPlaylist(playlist));
    }

    public void deleteSongInPlaylist(Song song, Playlist playlist, int songIndex){
        bll.deleteSongInPlaylist(song, playlist, songIndex);
    }
}
