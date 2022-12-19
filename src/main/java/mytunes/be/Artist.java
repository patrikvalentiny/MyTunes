package mytunes.be;

public class Artist {
    private int id;
    private String name;

    public Artist(String name) {
        this.name = name;
    }

    public Artist(int artistId, String artistName) {
        this(artistName);
        this.id = artistId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
