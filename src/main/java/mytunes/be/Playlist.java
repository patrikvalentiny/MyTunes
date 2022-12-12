package mytunes.be;

import static mytunes.be.tools.BETools.convertSecondsIntoString;

public class Playlist {
    private int id, totalLength;
    private String name;

    public Playlist(String name){
        this.name = name;
    }

    public Playlist(int id, String name) {
        this(name);
        this.id = id;
    }

    public Playlist(int id, String name, int totalLength) {
        this(id, name);
        this.totalLength = totalLength;
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

    public int getTotalLength() {
        return totalLength;
    }

    @Override
    public String toString() {
        return  "id = " + id +
                ", name = '" + name + '\'' +
                ", totalLength = " + totalLength;
    }

    public String getTotalLengthAsAString(){  // used by PropertyValueFactory
        return convertSecondsIntoString(this.totalLength);
    }
}
