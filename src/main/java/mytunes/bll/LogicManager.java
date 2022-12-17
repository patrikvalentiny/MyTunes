package mytunes.bll;

import mytunes.be.Genre;
import mytunes.be.Playlist;
import mytunes.be.Song;
import mytunes.dal.dao.GenreDAO;
import mytunes.dal.dao.PlaylistDAO;
import mytunes.dal.dao.SongDAO;
import mytunes.dal.interfaces.IGenreDataAccess;
import mytunes.dal.interfaces.IPlaylistDataAccess;
import mytunes.dal.interfaces.ISongDataAccess;

import java.util.ArrayList;
import java.util.List;

public class LogicManager implements LogicInterface{
    private static final ISongDataAccess songDAO = new SongDAO();
    private static final IGenreDataAccess genreDAO = new GenreDAO();
    private static final IPlaylistDataAccess playlistDAO = new PlaylistDAO();
    private static List<Song> allSongs;

    public void createSong(Song song) {
        songDAO.addSong(song);
    }

    public void deleteSong(Song song){
        songDAO.deleteSong(song);
    }

    public void updateSong(Song song) {
        songDAO.editSong(song);
    }

    public List<Song> getAllSongs() {
        allSongs = songDAO.getAllSongs();
        return allSongs;
    }
    public void createPlaylist(Playlist playlist){
        playlistDAO.addPlaylist(playlist);
    }

    public List<Playlist> getAllPlaylists() {
        for (Playlist p : playlistDAO.getAllPlaylists()){
            playlistDAO.calculateTotalLength(p.getId());
        }
        return playlistDAO.getAllPlaylists();
    }

    public void deletePlaylist(Playlist playlist) {
        playlistDAO.deletePlaylist(playlist);
    }

    public void updatePlaylist(Playlist playlist) {
        playlistDAO.updatePlaylist(playlist);
    }
    public List<Song> filterSongs(String query){
        List<Song> filteredSongs = new ArrayList<>();
        for (Song song : allSongs){
            if (song.getTitle().toLowerCase().trim().contains(query.toLowerCase().trim())
                    || song.getArtist().getName().toLowerCase().trim().contains(query.toLowerCase().trim())
                    || song.getGenre().getName().toLowerCase().trim().contains(query.toLowerCase().trim())){
                filteredSongs.add(song);
            }
        }
        return filteredSongs;
    }

    public void createGenre(Genre genre) {
        genreDAO.createGenre(genre);
    }

    public List<Genre> getAllGenres() {
        return genreDAO.getAllGenres();
    }

    public void addSongToPlaylist(Song song, Playlist playlist){
        playlistDAO.addSongToPlaylist(song.getId(), playlist.getId());
    }

    public List<Song> getSongsInPlaylist(Playlist playlist){
        return playlistDAO.getAllSongsInPlaylist(playlist.getId());
    }

    public void moveSongInPlaylist(Song song, Playlist playlist, Boolean moveUp, int songIndex) {
        playlistDAO.moveSongInPlaylist(song.getId(), playlist.getId(), moveUp, songIndex);
    }

    public void deleteSongInPlaylist(Song song, Playlist playlist, int songIndex){
        playlistDAO.deleteSongInPlaylist(song.getId(), playlist.getId(), songIndex);
    }
}
