package org.ellab.paru;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.ellab.paru.gui.SwingMain;
import org.ellab.paru.gui.SwtMain;
import org.ellab.paru.pattern.CharPattern;
import org.ellab.paru.pattern.DatePattern;
import org.ellab.paru.pattern.PasswordPattern;

public class Paru {
    public final static String APPNAME = "Paru";
    public final static String APPDESC = "Password Recovery Utilities";
    public final static String VERSION = "0.5";

    private ParuRunner tp;

    public interface ParuListener {
        void combinations(long combinations);

        void result(String result);

        void noresult();

        void error(String s, Exception ex);

        void progress(Performance perf, String current);
    }

    private static void showUsage(String[] args) {
        System.out.println();
        System.out.println("Usage:");
        System.out.println();
        System.out.println("zippass {no argument} - GUI");
        System.out.println("zippass --swing - GUI (Java Swing)");
        System.out.println("zippass file pattern args...");
        System.out.println();
        System.out.println("pattern:");
        System.out.println("  [date,yyyymmdd,yymmdd,ddmmyyyy,ddmmyy,ddmm,mmdd] start end prefix suffix");
        System.out.println("  date = yyyymmdd + yymmdd + ddmmyyyy + ddmmyy + ddmm + mmdd");
        System.out.println("  start - YYYYMMDD: date, number: n days ago ");
        System.out.println("     Example:");
        System.out.println("       yyyymmdd 20160101 20161231 foo bar");
        System.out.println("         foo20160101bar, foo20160102bar ... foo20161230bar, foo20161231bar");
        System.out.println("       yyyymmdd 20151231 20150101 foo,fool");
        System.out.println("         foo20161231, fool20161231, ... foo20160102, fool20160102");
        System.out.println("  [y1,y2,y3,...] prefix suffix");
    }

    public static String[] getDateCmdLineArgs(String file, int year, String prefix, String suffix, boolean display) {
        file = file == null ? "" : file;
        if (display) {
            if (file.trim().length() == 0) {
                file = "\"<Input Filename>\"";
            }
            else {
                file = "\"" + file + "\"";
            }
        }

        prefix = prefix != null ? prefix.trim() : "";
        suffix = suffix != null ? suffix.trim() : "";

        if (prefix.length() == 0 && suffix.length() > 0) {
            prefix = ",";
        }

        String[] args = { file, "y" + year, prefix, suffix };

        return args;
    }

    public static String[] getCharCmdLineArgs(String file, boolean numeric, boolean lower, boolean upper,
            boolean special, boolean space, String custom, int min, int max, String start, String end, String prefix,
            String suffix, boolean display) {
        file = file == null ? "" : file;
        if (display) {
            if (file.trim().length() == 0) {
                file = "\"<Input Filename>\"";
            }
            else {
                file = "\"" + file + "\"";
            }
        }

        min = Math.max(1, Math.min(min, max));
        max = Math.max(1, Math.max(min, max));
        start = start != null ? start.trim() : "";
        end = end != null ? end.trim() : "";
        prefix = prefix != null ? prefix.trim() : "";
        suffix = suffix != null ? suffix.trim() : "";

        String charPattern = CharPattern.getCharCmdlinePattern(numeric, lower, upper, special, space, custom);
        String pattern = CharPattern.CHAR_ALIAS.get(charPattern);
        // Impossible case as CharPattern.getCharCmdlinePattern always returns a valid pattern
        // if (pattern == null) {
        // charPattern = "\"Invalid Pattern {" + charPattern + "}\"";
        // }

        if (start.length() == 0 && end.length() > 0) {
            for (int i = 0; i < min; i++) {
                start += pattern.charAt(0);
            }
        }

        if (prefix.length() == 0 && suffix.length() > 0) {
            prefix = ",";
        }

        String[] args = { file, charPattern, "" + min, "" + max, start, end, prefix, suffix };
        if (args[4].length() == 0 && args[6].length() > 0) {
            // [4] == "", 5 have to == "", [7] > 0, [6] have to > 0
            args[4] = "_";
            args[5] = "_";
        }

        return args;
    }

    public static void main(String[] args) throws Exception {
        System.out.println();
        System.out.println(APPNAME + " " + VERSION + " - " + APPDESC);
        System.out.println();

        boolean useSwing = false;
        if (args.length == 1 && "--swing".equals(args[0])) {
            useSwing = true;
        }
        if (!useSwing && args.length == 0) {
            SwtMain swtMain = null;
            try {
                swtMain = new SwtMain(args);
            }
            catch (Error ex) {
                useSwing = true;
                System.out.println("Cannot start SWT GUI, use Swing instead.");
                System.out.println("Reason: " + ex.getMessage());
            }

            if (swtMain != null && !useSwing) {
                System.out.println("Start SWT GUI.");
                swtMain.show();
                return;
            }
        }

        if (useSwing) {
            System.out.println("Start Swing GUI.");
            SwingMain.main(args);
            return;
        }

        commandLineMain(args);
    }

    private static void commandLineMain(String[] args) throws ParseException {
        if (args.length < 2) {
            showUsage(args);
            System.exit(1);
        }

        if (!new File(args[0]).exists()) {
            System.out.println("File " + args[0] + " does not exist");
            System.exit(1);
        }

        try {
            Paru paru = new Paru(args, new ParuListener() {
                public void combinations(long combinations) {
                    System.out.println("Total " + combinations + " combinations");
                }

                public void result(String result) {
                    System.out.println();
                    System.out.println("Matched password is " + result);
                    System.out.println();
                }

                public void noresult() {
                    System.out.println();
                    System.out.println(Constants.MSG_NO_RESULT);
                }

                public void error(String s, Exception ex) {
                    System.out.println();
                    if (ex == null) {
                        System.out.println(s);
                    }
                    else {
                        System.out.println("Error: " + s);
                        ex.printStackTrace();
                    }
                }

                public void progress(Performance perf, String current) {
                    System.out.println(perf.getResultDescWithAvg("Processed")
                            + (current != null ? (" (" + current + ")") : ""));
                }
            }, 1000);

            paru.start();
        }
        catch (IOException ex) {
            // Should report in listener;
        }
    }

    public Paru(String[] args, ParuListener listener, int progressInterval) throws ParseException, IOException {
        String filename = args[0];

        String[] allDatePattern = new String[] { "yyyymmdd", "yymmdd", "ddmmyyyy", "ddmmyy", "ddmm", "mmdd" };
        if (args[1].matches("^y\\d+")) {
            String[] datePattern = allDatePattern;
            String first = "0";
            String last = "" + (Integer.valueOf(args[1].substring(1)) * 366);
            String prefix = args.length > 2 ? args[2] : null;
            String suffix = args.length > 3 ? args[3] : null;
            List<PasswordPattern> pp = new ArrayList<PasswordPattern>();
            addDatePattern(pp, datePattern, first, last, prefix, suffix);
            tp = ParuRunner.create(filename, listener, progressInterval, pp);
        }
        else if ("date".equals(args[1]) || "yyyymmdd".equals(args[1]) || "yymmdd".equals(args[1])
                || "ddmmyyyy".equals(args[1]) || "ddmmyy".equals(args[1]) || "ddmm".equals(args[1])
                || "mmdd".equals(args[1])) {
            String[] datePattern = null;
            if ("date".equals(args[1])) {
                datePattern = allDatePattern;
            }
            else {
                datePattern = new String[] { args[1] };
            }
            String first = args[2];
            String last = args[3];
            String prefix = args.length > 4 ? args[4] : null;
            String suffix = args.length > 5 ? args[5] : null;
            List<PasswordPattern> pp = new ArrayList<PasswordPattern>();
            addDatePattern(pp, datePattern, first, last, prefix, suffix);
            tp = ParuRunner.create(filename, listener, progressInterval, pp);
        }
        else if (CharPattern.CHAR_ALIAS.get(args[1]) != null) {
            int minLen = Integer.parseInt(args[2]);
            int maxLen = Integer.parseInt(args[3]);
            String first = args.length > 4 ? args[4] : null;
            String last = args.length > 5 ? args[5] : null;
            String prefix = args.length > 6 ? args[6] : null;
            String suffix = args.length > 7 ? args[7] : null;
            if (first != null && last != null && first.equals(last)) {
                first = null;
                last = null;
            }
            first = "_".equals(first) ? "" : null;
            last = "_".equals(last) ? "" : null;
            tp = ParuRunner.createFromCharPattern(filename, listener, progressInterval,
                    CharPattern.CHAR_ALIAS.get(args[1]), minLen, maxLen, first, last, prefix, suffix);
        }
        else {
            throw new ParseException("Invalid pattern " + args[1], 1);
        }
    }

    public void start() {
        tp.start();
    }

    private static void addDatePattern(List<PasswordPattern> pp, String[] datePattern, String first, String last,
            String prefix, String suffix) throws ParseException {
        for (int i = 0; i < datePattern.length; i++) {
            pp.add(new DatePattern(datePattern[i].replaceAll("m", "M"), first, last, prefix, suffix));
        }
    }

    public boolean isRunning() {
        return tp.isRunning();
    }

    public void pause() {
        tp.pause();
    }

    public void resume() {
        tp.resume();
    }

    public void stop() {
        tp.stop();
    }
}