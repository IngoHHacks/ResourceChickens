package tv.ingoh.util;

public class Pluralize {
    
    /**
     * @param count An integer
     * @return {@code "is"} if count == 1, {@code "are"} if count != 1
     */
    public static String are(int count) {
        if (count == 1) return "is";
        else return "are";
    }

    /**
     * @param count An integer
     * @return {@code ""} if count == 1, {@code "s"} if count != 1
     */
    public static String s(int count) {
        if (count == 1) return "";
        else return "s";
    }
}
