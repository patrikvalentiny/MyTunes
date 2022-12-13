package mytunes.dal.dao;

import mytunes.be.Artist;
import mytunes.dal.interfaces.IArtistDataAccess;

import java.sql.ResultSet;
import java.sql.SQLException;

import static mytunes.dal.DAOTools.*;

public class ArtistDAO implements IArtistDataAccess {

    public Artist getArtist(int id) {
        String sql = "SELECT * FROM ARTISTS WHERE id = " + id;
        try (ResultSet rs = SQLQueryWithRS(sql)) {
            rs.next();
            int artistId = rs.getInt("id");
            String artistName = rs.getString("name");
            return new Artist(artistId, artistName);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void addArtist(Artist artist) {
        String sql = "INSERT INTO ARTISTS (name) " +
                "VALUES ('" + validateStringForSQL(artist.getName()) + "')";
        try {
            SQLQuery(sql);
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE_name")) {
                System.out.println("Artist already exists");
                sql = "SELECT * FROM ARTISTS WHERE name = '" + validateStringForSQL(artist.getName()) + "'";
                try (ResultSet rs = SQLQueryWithRS(sql)) {
                    rs.next();
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else
                throw new RuntimeException(e);
            throw new RuntimeException(e);
        }
    }
}
