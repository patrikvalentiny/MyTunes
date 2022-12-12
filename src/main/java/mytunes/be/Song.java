package mytunes.be;

import static mytunes.be.tools.BETools.convertSecondsIntoString;

public class Song {
    private int id;
    private final int duration;
    private final String title, path;
    private final Artist artist;
    private Genre genre;
    private int indexInPlaylist;

    public Song(String title, Artist artist, Genre genre, String path,  int duration) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.path = path;
        this.genre = genre;
    }

    public Song(int id, String title, Artist artist, Genre genre, String path, int duration) {
        this(title, artist, genre, path, duration);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Artist getArtist() {
        return artist;
    }

    public String getPath() {
        return path;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public int getDuration() {
        return duration;
    }

    public String toString(){
        return  "ID: " + id + ", Title: " + title + ", Artist: " + artist.getName() + ", Genre: " + genre.getName() +
                ", Duration: " + duration + ", Path: " + path;
    }

    public String getDurationAsAString(){  // used by PropertyValueFactory
        return convertSecondsIntoString(this.duration);
    }

    public int getIndexInPlaylist() {
        return indexInPlaylist;
    }

    public void setIndexInPlaylist(int indexInPlaylist) {
        this.indexInPlaylist = indexInPlaylist;
    }
}
