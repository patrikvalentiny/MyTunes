package mytunes.dal.interfaces;

import mytunes.be.Playlist;
import mytunes.be.Song;

import java.sql.SQLException;
import java.util.List;


public interface IPlaylistDataAccess {
    /**
     * Retrieves all playlists in the database and puts them into a list
     * @return a list of all the playlists in the database
     */
    List<Playlist> getAllPlaylists();

    /**
     * Retrieves information about a Playlist from the database and
     * returns it as a new Playlist object
     * @param id the id of the Playlist to be retrieved
     * @return a Playlist with the desired id
     */
    Playlist getPlaylist(int id);

    /**
     * Saves the information about a new playlist into the database
     * @param playlist the playlist to be added
     */
    void addPlaylist(Playlist playlist) throws SQLException;

    /**
     * Deletes the desired playlist from the playlist database, as well as
     * the linking song/playlist database
     * @param playlist the playlist to be deleted
     */
    void deletePlaylist(Playlist playlist);

    /**
     * Updates the name of the playlist within the database
     * @param playlist the playlist to be updated
     */
    void updatePlaylist(Playlist playlist) throws SQLException;

    /**
     * Retrieves all songs in a playlist and sorts them by index in playlist
     * @param playlistID the id of a playlist we're getting the songs from
     * @return a list of all songs in playlist sorted by their index
     */
    List<Song> getAllSongsInPlaylist(int playlistID);

    /**
     * Puts the selected song into a selected playlist and recalculates
     * the length of the playlist and assigns an index for the added song
     * @param songID the id of the song to be added
     * @param playlistID the id of the playlist, where a song should be added
     */
    void addSongToPlaylist(int songID, int playlistID);

    /**
     * If possible to move the song up/down by an index, the indices of the selected song and the song before/after will be swapped.
     * @param songID the ID of the song that should be moved
     * @param playlistID the ID of the playlist, where we're moving the song
     * @param moveUp indicates, whether we're moving the song up or down in the playlist
     * @param songIndex the current index of the song
     */
    void moveSongInPlaylist(int songID, int playlistID, boolean moveUp, int songIndex);

    /**
     * When a song is added to database, this method updates its index in the playlist (always added to the end)
     * @param songID the song, whose index we are updating
     * @param playlistID the playlist, where the song is located
     */
    void updateIndexInPlaylist(int songID, int playlistID);

    /**
     * Deletes the song from a playlist and moves all consequential songs by one index forward
     * @param songID the ID of the song we're deleting
     * @param playlistID the ID of the playlist, in which we're deleting the song
     * @param songIndex the current index of the song
     */
    void deleteSongInPlaylist(int songID, int playlistID, int songIndex);

    /**
     * Calculates the total length of the playlist from the durations of the songs
     * within the playlist and updates the total length in the database
     * @param playlistID the ID of the playlist we're calculating the length of
     */
    void calculateTotalLength(int playlistID);
}
