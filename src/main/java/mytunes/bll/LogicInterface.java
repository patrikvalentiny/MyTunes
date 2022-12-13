package mytunes.bll;

import mytunes.be.Genre;
import mytunes.be.Playlist;
import mytunes.be.Song;

import java.util.List;

public interface LogicInterface {
    //working with songs

    /**
     * Calls the addSong() method in songDAO
     * @param song the song to be created
     */
    public void createSong(Song song);

    /**
     * Calls the deleteSong() method in songDAO
     * @param song the song to be deleted
     */
    public void deleteSong(Song song);

    /**
     * Calls the editSong() method in songDAO
     * @param song the song to be updated
     */
    public void updateSong(Song song);

    /**
     * Calls a getAllSongs() method in songDAO
     * @return a list of all songs in the database
     */
    public List<Song> getAllSongs();

    /**
     *
     */
    public void loadSongsToMemory();

    /**
     *
     */
    public void removeSongsFromMemory();

    /**
     * Filters songs based on a query
     * @param query A user's search query
     * @return a list of songs, whose metadata (title, artist or genre)
     * contain the query
     */
    public List<Song> filterSongs(String query);


    //working with playlists

    /**
     * calls the addPlaylist() method in playlistDAO
     * @param playlist the playlist to be created
     */
    public void createPlaylist(Playlist playlist);

    /**
     * calls the deletePlaylist() method in playlistDAO
     * @param playlist the playlist to be deleted
     */
    public void deletePlaylist(Playlist playlist);

    /**
     * calls the updatePlaylist() method in playlistDAO
     * @param playlist the playlist to be updated
     */
    public void updatePlaylist(Playlist playlist);

    /**
     * Calls a getAllPlaylists() method in playlistDAO
     * @return a list of all playlists in the database
     */
    public List<Playlist> getAllPlaylists();


    //working with genres
    public void createGenre(Genre genre);
    public List<Genre> getAllGenres();


    //working with songs in playlists
    public void addSongToPlaylist(Song song, Playlist playlist);

    public List<Song> getSongsInPlaylist(Playlist playlist);

    public void moveSongInPlaylist(Song song, Playlist playlist, Boolean moveUp, int songIndex);

    public void deleteSongInPlaylist(Song song, Playlist playlist, int songIndex);
}
