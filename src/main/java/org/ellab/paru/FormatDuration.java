package org.ellab.paru;

public class FormatDuration {
    public String formatDuration(long m) {
        m = Math.max(0, m);

        long sec = (m / 1000) % 60;
        long min = (m / 1000 / 60) % 60;
        long hour = m / 1000 / 60 / 60;
        long day = m / 1000 / 60 / 60 / 24;

        if (m > 48 * 3600 * 1000) {
            if (hour % 24 > 0) {
                ++day;
            }
            return day + " day" + (day > 1 ? "s" : "");
        }
        else {
            String hourStr = ((day > 0 || hour > 0) ? hour + ":" : "");
            String minStr = ((min < 10 && hour > 0 ? "0" : "") + min);
            String secStr = ":" + (sec < 10 ? "0" : "") + sec;

            return hourStr + minStr + secStr;
        }
    }

    public String formatDurationLong(long m) {
        if (m <= 48 * 3600 * 1000) {
            return formatDuration(m);
        }

        long sec = (m / 1000) % 60;
        long min = (m / 1000 / 60) % 60;
        long hour = (m / 1000 / 60 / 60) % 24;
        long day = m / 1000 / 60 / 60 / 24;

        if (day > 2 && hour == 0 && min == 0 && sec == 0) {
            return day + " days";
        }
        else {
            String dayStr = day > 0 ? day + " day" + (day > 1 ? "s" : "") + " " : "";
            // String hourStr = ((day > 0 || hour > 0) ? (hour < 10 ? "0" : "") + hour + ":" : "");
            String hourStr = (day > 0 || hour > 0) ? (hour + ":") : "";
            String minStr = ((min < 10 && (day > 0 || hour > 0) ? "0" : "") + min);
            String secStr = ":" + (sec < 10 ? "0" : "") + sec;

            return dayStr + hourStr + minStr + secStr;
        }
    }

    public static void main(String[] args) {
        testFormatDuration("0:32", 0, 0, 0, 0, 0, 32);
        testFormatDuration("1:12", 0, 0, 0, 0, 0, 72);
        testFormatDuration("1:04", 0, 0, 0, 0, 1, 4);
        testFormatDuration("1:34", 0, 0, 0, 0, 1, 34);
        testFormatDuration("8:01:34", 0, 0, 0, 8, 1, 34);
        testFormatDuration("25:01:01", "25:01:01", 0, 0, 1, 1, 1, 1);
        testFormatDuration("47:59:59", "47:59:59", 0, 0, 1, 23, 59, 59);
        testFormatDuration("24:00:00", "24:00:00", 0, 0, 1, 0, 0, 0);
        testFormatDuration("48:00:00", "48:00:00", 0, 0, 2, 0, 0, 0);
        testFormatDuration("2 days", "2 days 0:00:01", 0, 0, 2, 0, 0, 1);
        testFormatDuration("2 days", "2 days 0:59:59", 0, 0, 2, 0, 59, 59);
        testFormatDuration("3 days", "3 days", 0, 0, 3, 0, 0, 0);
        testFormatDuration("3 days", "2 days 1:00:00", 0, 0, 2, 1, 0, 0);
        testFormatDuration("6 days", "5 days 1:00:00", 0, 0, 5, 1, 0, 0);
        testFormatDuration("31 days", "30 days 1:00:00", 0, 0, 30, 1, 0, 0);
        testFormatDuration("32 days", "31 days 1:00:00", 0, 0, 31, 1, 0, 0);
        testFormatDuration("60 days", "59 days 1:00:00", 0, 0, 59, 1, 0, 0);
        testFormatDuration("61 days", "60 days 1:00:00", 0, 0, 60, 1, 0, 0);
    }

    private static String padRight(String s, int n) {
        while (s.length() <= n) {
            s += ' ';
        }

        return s;
    }

    private static void testFormatDuration(String expected, int year, int month, int day, int hour, int minute,
            int second) {
        testFormatDuration(expected, expected, year, month, day, hour, minute, second);
    }

    private static void testFormatDuration(String expected1, String expected2, int year, int month, int day, int hour,
            int minute, int second) {

        long millis = (year * 86400l * 30 * 12 + month * 86400l * 30 + day * 86400l + hour * 3600l + minute * 60l + second) * 1000l;
        String actual1 = new FormatDuration().formatDuration(millis);
        String actual2 = new FormatDuration().formatDurationLong(millis);

        if (actual1.equals(expected1)) {
            if (actual2.equals(expected2)) {
                System.out.println("PASSED    " + padRight(actual1, 16) + "     " + padRight(actual2, 16));
            }
            else {
                System.out.println("FAILED 2  expected=" + expected2 + ", actual=" + actual2);
            }
        }
        else {
            System.out.println("FAILED 1  expected=" + expected1 + ", actual=" + actual1);
        }
    }
}
