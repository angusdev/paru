package org.ellab.paru;

public interface Constants {
    public static String ICON = "/zip.ico";
    public static String URL = "https://www.github.com/angusdev/paru/";

    public static String MSG_NO_RESULT = "No matching password";
    public static String MSG_WORKING = "Working";
    public static String MSG_FILE_NOT_FOUND = "File not found";
    public static String MSG_INVALID_FILE_FORMAT = "Not a valid file";
    public static String MSG_FILE_NOT_ENCRYPTED = "File is not encrypted";

    public static String BTN_START = "Start";
    public static String BTN_PAUSE = "Pause";
    public static String BTN_RESUME = "Resume";

    public static String LIB_POI = "poi 3.15";
    public static String LIB_ZIP4J = "zip4j 1.3.2";
    public static String LIB_ITEXT = "itext 5.5.10";
    public static String LIB_ALL = LIB_ZIP4J + " " + LIB_ITEXT + " " + LIB_POI;
}
