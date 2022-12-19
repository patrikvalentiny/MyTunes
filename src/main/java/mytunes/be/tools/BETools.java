package mytunes.be.tools;

public class BETools {

    public static String convertSecondsIntoString(int duration){
        int seconds = duration % 60;
        int minutes = (duration / 60) % 60;
        int hours = (duration / 60) / 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
}
