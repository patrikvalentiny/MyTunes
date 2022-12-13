package mytunes.dal.interfaces;

import mytunes.be.Genre;

import java.util.List;

public interface IGenreDataAccess {
    /**
     * Retrieves all genres in the database and puts them into a list
     * @return a list of all the genres in the database
     */
    public List<Genre> getAllGenres();

    /**
     * Retrieves information about a Genre from the database and
     * returns it as a new Genre object
     * @param id the id of the Genre to be retrieved
     * @return a Genre with the desired id
     */
    public Genre getGenre(int id);

    /**
     * Attempts to put a genre into the database, fails if the genre
     * already exists
     * @param genre the Genre to be added to the database
     */
    public void createGenre(Genre genre);
}
