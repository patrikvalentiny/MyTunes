package mytunes.dal.interfaces;

import mytunes.be.Artist;

public interface IArtistDataAccess {
    /**
     * Retrieves information about an Artist from the database and
     * returns it as a new Artist object
     * @param id the id of the Artist to be retrieved
     * @return an Artist with the desired id
     */
    public Artist getArtist(int id);

    /**
     * Attempts to put an artist into the database, fails if the artist
     * already exists
     * @param artist the artist to be added to the database
     */
    public void addArtist(Artist artist);
}
