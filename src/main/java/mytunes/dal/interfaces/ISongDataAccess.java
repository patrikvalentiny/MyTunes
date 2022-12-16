package mytunes.dal.interfaces;

import mytunes.be.Song;

import java.util.List;

public interface ISongDataAccess {
    /**
     * Retrieves all songs in the database and puts them into a list
     * @return a list of all the songs in the database
     */
    List<Song> getAllSongs();

    /**
     * Retrieves information about a Song from the database and
     * returns it as a new Song object
     * @param id the id of the Song to be retrieved
     * @return a Song with the desired id
     */
    Song getSong(int id);

    /**
     * Adds a song to the database, if a song with this name already exists, an alert window is called
     * @param song the song to be added
     */
    void addSong(Song song);

    /**
     * Updates the metadata of the song within the database based on the songID
     * @param song the song to be updated
     */
    void editSong(Song song);

    /**
     * Deletes the desired song from the song database, as well as
     * the linking song/playlist database
     * @param song the song to be deleted
     */
    void deleteSong(Song song);
}
