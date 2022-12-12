package mytunes.be.tools;

public class BETools {

    public static String convertSecondsIntoString(int duration){
        String totalLengthAsAString = "";
        int seconds = duration % 60;
        int minutes = (duration / 60) % 60;
        int hours = (duration / 60) / 60;

        if (hours > 0){
            if (hours < 10)
                totalLengthAsAString += "0";
            totalLengthAsAString += hours + ":";
        }

        if (minutes < 10)
            totalLengthAsAString += "0";
        totalLengthAsAString += minutes + ":";

        if (seconds < 10)
            totalLengthAsAString += "0";
        totalLengthAsAString += seconds;

        return totalLengthAsAString;
    }
}
