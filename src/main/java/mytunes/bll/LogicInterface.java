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
     * Calls the getAllSongs() method in songDAO and saves the songs in
     * a list of allSongs
     */
    public void loadSongsToMemory();

    /**
     * Clears the list of allSongs
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

    /**
     * calls the createGenre() method in genreDAO
     * @param genre the genre to be created
     */
    public void createGenre(Genre genre);

    /**
     * Calls a getAllGenres() method in genreDAO
     * @return a list of all genres in the database
     */
    public List<Genre> getAllGenres();


    //working with songs in playlists

    /**
     * Calls the addSongToPlaylist() method in playlistDAO
     * @param song the song to be added
     * @param playlist the playlist, where a song is to be added
     */
    public void addSongToPlaylist(Song song, Playlist playlist);

    /**
     * Calls the getAllSongsInPlaylist() method in playlistDAO
     * @param playlist the playlist, from which songs need to be retrieved
     * @return a list of all songs in the selected playlist
     */
    public List<Song> getSongsInPlaylist(Playlist playlist);

    /**
     * Calls the moveSongInPlaylist() method in playlistDAO
     * @param song the song to be moved
     * @param playlist the playlist containing the song
     * @param moveUp indicates, if the song is to be moved up or down
     * @param songIndex the current index of the song within the playlist
     */
    public void moveSongInPlaylist(Song song, Playlist playlist, Boolean moveUp, int songIndex);

    /**
     * Calls the deleteSongInPlaylist() method in playlistDAO
     * @param song the song to be deleted
     * @param playlist the playlist containing the song
     * @param songIndex the current index of the song within the playlist
     */
    public void deleteSongInPlaylist(Song song, Playlist playlist, int songIndex);
}
