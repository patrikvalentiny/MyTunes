package mytunes.dal.dao;

import mytunes.be.Playlist;
import mytunes.be.Song;
import mytunes.dal.interfaces.IPlaylistDataAccess;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static mytunes.dal.DAOTools.*;

public class PlaylistDAO implements IPlaylistDataAccess {

    public List<Playlist> getAllPlaylists() {
        ArrayList<Playlist> allPlaylists = new ArrayList<>();
        String sql = "SELECT * FROM ALL_PLAYLISTS";
        try (ResultSet rs = SQLQueryWithRS(sql)){
            while (rs.next()) {
                int id = rs.getInt("id");
                String playlistName = rs.getString("playlistName");
                int totalLength = rs.getInt("total_length");
                Playlist playlist = new Playlist(id, playlistName, totalLength);
                allPlaylists.add(playlist);
            }
            return allPlaylists;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Playlist getPlaylist(int id) {
        String sql = "SELECT * FROM ALL_PLAYLISTS WHERE id = " + id;
        try  (ResultSet rs = SQLQueryWithRS(sql)){
            rs.next();
            return new Playlist(id, rs.getString("playlistName"), rs.getInt("total_length"));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addPlaylist(Playlist playlist) throws SQLException {
        String sql = "INSERT INTO ALL_PLAYLISTS (playlistName, total_length) " +
                "VALUES ('" + validateStringForSQL(playlist.getName()) + "', "
                + playlist.getTotalLength() + ")";
        SQLQuery(sql);
    }


    public void deletePlaylist(Playlist playlist) {
        int id = playlist.getId();
        String sql = "DELETE FROM ALL_PLAYLISTS WHERE id =" + id;
        String sql_delete_from_link = "DELETE FROM SONG_PLAYLIST_LINK WHERE playlistID = " + id;
        try  {
            SQLQuery(sql_delete_from_link);
            SQLQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updatePlaylist(Playlist playlist) throws SQLException {
        String sql = "UPDATE ALL_PLAYLISTS SET playlistName = '" + validateStringForSQL(playlist.getName()) + "', "
                + "total_length = '" + playlist.getTotalLength() + "' "
                + "WHERE id = " + playlist.getId();
        SQLQuery(sql);
    }

    public List<Song> getAllSongsInPlaylist(int playlistID) {
        ArrayList<Song> songsInPlaylist = new ArrayList<>();
        String sql = "SELECT playlistID, songID, songIndex FROM SONG_PLAYLIST_LINK WHERE playlistId = " + playlistID + " ORDER BY songIndex";
        try (ResultSet rs = SQLQueryWithRS(sql)) {
            while (rs.next()) {
                int songId = rs.getInt("songId");
                int songIndex = rs.getInt("songIndex");
                SongDAO songDAO = new SongDAO();
                Song song = songDAO.getSong(songId);
                song.setIndexInPlaylist(songIndex);
                songsInPlaylist.add(song);
            }
            return songsInPlaylist;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void addSongToPlaylist(int songID, int playlistID) {
        String select = "SELECT TOP 1 playlistID, songIndex FROM SONG_PLAYLIST_LINK WHERE playlistId = " + playlistID + " ORDER BY songIndex DESC";
        try {
            ResultSet rs  = SQLQueryWithRS(select);
            int songIndex = 1;
            while (rs.next()) {
                songIndex = rs.getInt("songIndex") + 1;
            }
            String sql = "INSERT INTO SONG_PLAYLIST_LINK (songId, playlistId, songIndex) VALUES (" + songID + ", " + playlistID + ", " + songIndex + ")";
            SQLQuery(sql);
            calculateTotalLength(playlistID);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void moveSongInPlaylist(int songID, int playlistID, boolean moveUp, int songIndex){
        int newIndex;
        if (moveUp) {
             newIndex = songIndex - 1; // new index has to be one less than old index
         } else {
             newIndex = songIndex + 1; // new index has to be one more than old index
         }
        String sql = "UPDATE SONG_PLAYLIST_LINK SET songIndex = " + songIndex + " WHERE songIndex = " + newIndex + " ; "+ // set song's index at this position to old index
                     "UPDATE SONG_PLAYLIST_LINK SET songIndex = " + newIndex + " WHERE playlistID = " + playlistID +
             " AND songID = " + songID + " AND songIndex = " + songIndex + " ; "; // set song's index to new index

        try {
            SQLQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateIndexInPlaylist(int songID, int playlistID){
        int songIndex = getAllSongsInPlaylist(playlistID).size();
        String sql = "UPDATE SONG_PLAYLIST_LINK SET songIndex = " + songIndex + " WHERE playlistId = "
                + playlistID + " AND songId = " + songID + " AND songIndex IS NULL";
        try{
            SQLQuery(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void deleteSongInPlaylist(int songID, int playlistID, int songIndex){
        try {
            String sql = "DELETE FROM SONG_PLAYLIST_LINK WHERE playlistID = " + playlistID +
                    " AND songID = " + songID + " AND songIndex = " + songIndex+ " ; " +
                    "UPDATE SONG_PLAYLIST_LINK SET songIndex = songIndex - 1 WHERE playlistID = " + playlistID +
                    " AND songIndex > " + songIndex;
            SQLQuery(sql);
            calculateTotalLength(playlistID);
        } catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public void calculateTotalLength(int playlistID){
        try {
            String sql = "UPDATE ALL_PLAYLISTS SET total_length = (SELECT SUM(duration) FROM SONG_PLAYLIST_LINK " +
                    "INNER JOIN ALL_SONGS ON SONG_PLAYLIST_LINK.songID = ALL_SONGS.id " +
                    "WHERE playlistID = " + playlistID + ") WHERE id = " + playlistID;
            SQLQuery(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
