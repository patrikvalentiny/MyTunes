package mytunes.bll;

import mytunes.be.Genre;
import mytunes.be.Playlist;
import mytunes.be.Song;

import java.sql.SQLException;
import java.util.List;

public interface LogicInterface {
    //working with songs

    /**
     * Calls the addSong() method in songDAO
     * @param song the song to be created
     */
    void createSong(Song song);

    /**
     * Calls the deleteSong() method in songDAO
     * @param song the song to be deleted
     */
    void deleteSong(Song song);

    /**
     * Calls the editSong() method in songDAO
     * @param song the song to be updated
     */
    void updateSong(Song song);

    /**
     * Calls a getAllSongs() method in songDAO
     * @return a list of all songs in the database
     */
    List<Song> getAllSongs();

    /**
     * Filters songs based on a query
     * @param query A user's search query
     * @return a list of songs, whose metadata (title, artist or genre)
     * contain the query
     */
    List<Song> filterSongs(String query);


    //working with playlists

    /**
     * calls the addPlaylist() method in playlistDAO
     * @param playlist the playlist to be created
     */
    void createPlaylist(Playlist playlist) throws SQLException;

    /**
     * calls the deletePlaylist() method in playlistDAO
     * @param playlist the playlist to be deleted
     */
    void deletePlaylist(Playlist playlist);

    /**
     * calls the updatePlaylist() method in playlistDAO
     * @param playlist the playlist to be updated
     */
    void updatePlaylist(Playlist playlist) throws SQLException;

    /**
     * Calls a getAllPlaylists() method in playlistDAO
     * @return a list of all playlists in the database
     */
    List<Playlist> getAllPlaylists();


    //working with genres

    /**
     * calls the createGenre() method in genreDAO
     * @param genre the genre to be created
     */
    void createGenre(Genre genre);

    /**
     * Calls a getAllGenres() method in genreDAO
     * @return a list of all genres in the database
     */
    List<Genre> getAllGenres();


    //working with songs in playlists

    /**
     * Calls the addSongToPlaylist() method in playlistDAO
     * @param song the song to be added
     * @param playlist the playlist, where a song is to be added
     */
    void addSongToPlaylist(Song song, Playlist playlist);

    /**
     * Calls the getAllSongsInPlaylist() method in playlistDAO
     * @param playlist the playlist, from which songs need to be retrieved
     * @return a list of all songs in the selected playlist
     */
    List<Song> getSongsInPlaylist(Playlist playlist);

    /**
     * Calls the moveSongInPlaylist() method in playlistDAO
     * @param song the song to be moved
     * @param playlist the playlist containing the song
     * @param moveUp indicates, if the song is to be moved up or down
     * @param songIndex the current index of the song within the playlist
     */
    void moveSongInPlaylist(Song song, Playlist playlist, Boolean moveUp, int songIndex);

    /**
     * Calls the deleteSongInPlaylist() method in playlistDAO
     * @param song the song to be deleted
     * @param playlist the playlist containing the song
     * @param songIndex the current index of the song within the playlist
     */
    void deleteSongInPlaylist(Song song, Playlist playlist, int songIndex);
}
