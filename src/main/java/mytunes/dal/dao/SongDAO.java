package mytunes.dal.dao;

import javafx.scene.control.Alert;
import mytunes.be.Artist;
import mytunes.be.Genre;
import mytunes.be.Song;
import mytunes.dal.interfaces.ISongDataAccess;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static mytunes.dal.DAOTools.*;

public class SongDAO implements ISongDataAccess {

    public List<Song> getAllSongs() {
        ArrayList<Song> allSongs = new ArrayList<>();
        String sql = "SELECT * FROM ALL_SONGS";
        try (ResultSet rs = SQLQueryWithRS(sql)){
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String artist= rs.getString("artist");
                String genre = rs.getString("genre");
                int duration = rs.getInt("duration");
                String path = rs.getString("filepath");
                Song song = new Song(id, title, new Artist(artist), new Genre(genre), path, duration);
                allSongs.add(song);
            }
            return allSongs;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Song getSong(int id) {
        String sql = "SELECT * FROM ALL_SONGS WHERE id = " + id;
        try (ResultSet rs = SQLQueryWithRS(sql)){
            rs.next();
            return new Song(id, rs.getString("title"), new Artist(rs.getString("artist")),
                    new Genre(rs.getString("genre")), rs.getString("filepath"), rs.getInt("duration"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addSong(Song song){
        String sql = "INSERT INTO ALL_SONGS (title, artist, genre, filepath, duration) " +
                        "VALUES ('" + validateStringForSQL(song.getTitle()) + "', '"
                        + validateStringForSQL(song.getArtist().getName()) + "', '"
                        + validateStringForSQL(song.getGenre().getName()) + "', '"
                        + validateStringForSQL(song.getPath()) + "', "
                        + song.getDuration() + ")";
        try {
            SQLQuery(sql);
        } catch (SQLException e) {
            if (e.getMessage().contains("C_unique_title"))
            {
                new Alert(Alert.AlertType.ERROR, "Song already exists").showAndWait();
            } else
                throw new RuntimeException(e);
        }
    }

    public void editSong(Song song){
        String sql = "UPDATE ALL_SONGS SET title = '" + validateStringForSQL(song.getTitle()) + "', "
                + "artist = '" + validateStringForSQL(song.getArtist().getName()) + "', "
                + "genre = '" + validateStringForSQL(song.getGenre().getName()) + "', "
                + "filepath = '" + validateStringForSQL(song.getPath()) + "', "
                + "duration = " + song.getDuration() + " "
                + "WHERE id = " + song.getId();
        try {
            SQLQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteSong(Song song){
        int id  = song.getId();
        String sql = "DELETE FROM SONG_PLAYLIST_LINK WHERE songID = " + id + "; " +
                "DELETE FROM ALL_SONGS WHERE id = " + id;
        try {
            SQLQuery(sql);
            reindexSongs();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void reindexSongs(){
        String sql;
        ArrayList<Integer> playlistIDs = new ArrayList<>();
        try {
            sql = "SELECT DISTINCT playlistID FROM SONG_PLAYLIST_LINK";
            ResultSet rs = SQLQueryWithRS(sql);
            while (rs.next()){
                playlistIDs.add(rs.getInt("playlistID"));
            }

            for (Integer playlistID : playlistIDs) {
                rs = SQLQueryWithRS("SELECT * FROM SONG_PLAYLIST_LINK WHERE playlistID = " + playlistID + " ORDER BY songIndex");
                int j = 1;
                while (rs.next()) {
                    int currentSongIndex = rs.getInt("songIndex");
                    sql = "UPDATE SONG_PLAYLIST_LINK SET songIndex = " + j + " WHERE songIndex = " + currentSongIndex
                            + " AND playlistID = " + playlistID;
                    SQLQuery(sql);
                    j++;
                }
            }
        } catch (SQLException e) {
             throw new RuntimeException(e);
        }
    }
}
