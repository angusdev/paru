package org.ellab.paru.pattern;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ellab.paru.Performance;

public class CharPattern extends PrefixSuffixPattern {
    public static String NUMERIC = "0123456789";
    public static String LOWER = "abcdefghijklmnopqrstuvwxyz";
    public static String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static String SPECIAL = "`~!@#$%^&*()-_=+[]{}\\|;:'\",<.>/? ";
    public static String SPACE = " ";
    public static String ALPHA = LOWER + UPPER;
    public static String LOWER_NUMERIC = NUMERIC + LOWER;
    public static String UPPER_NUMERIC = NUMERIC + UPPER;
    public static String ALPHA_NUMERIC = NUMERIC + ALPHA;
    public static String NUMERIC_SPECIAL = NUMERIC + SPECIAL;
    public static String LOWER_SPECIAL = LOWER + SPECIAL;
    public static String UPPER_SPECIAL = UPPER + SPECIAL;
    public static String ALPHA_SPECIAL = ALPHA + SPECIAL;
    public static String LOWER_NUMERIC_SPECIAL = LOWER_NUMERIC + SPECIAL;
    public static String UPPER_NUMERIC_SPECIAL = UPPER_NUMERIC + SPECIAL;
    public static String VISIBLE = ALPHA_NUMERIC + SPECIAL;
    public static String ALPHA_NUMERIC_SPECIAL = ALPHA_NUMERIC + SPECIAL;
    public static String NUMERIC_SPACE = NUMERIC + " ";
    public static String LOWER_SPACE = LOWER + " ";
    public static String UPPER_SPACE = UPPER + " ";
    public static String ALPHA_SPACE = ALPHA + " ";
    public static String LOWER_NUMERIC_SPACE = LOWER_NUMERIC + " ";
    public static String UPPER_NUMERIC_SPACE = UPPER_NUMERIC + " ";
    public static String ALPHA_NUMERIC_SPACE = ALPHA_NUMERIC + " ";

    public static String ALIAS_NUMERIC = "0-9";
    public static String ALIAS_LOWER = "a-z";
    public static String ALIAS_UPPER = "A-Z";
    public static String ALIAS_SPECIAL = "!";
    public static String ALIAS_SPACE = ":w";

    public static Map<String, String> CHAR_ALIAS = new HashMap<String, String>();
    static {
        CHAR_ALIAS.put("0-9", NUMERIC);
        CHAR_ALIAS.put("!", SPECIAL);
        CHAR_ALIAS.put("!:w", SPECIAL);
        CHAR_ALIAS.put(":w", SPACE);
        CHAR_ALIAS.put("a-z", LOWER);
        CHAR_ALIAS.put("A-Z", UPPER);
        CHAR_ALIAS.put("a-zA-Z", ALPHA);
        CHAR_ALIAS.put("0-9a-z", LOWER_NUMERIC);
        CHAR_ALIAS.put("0-9A-Z", UPPER_NUMERIC);
        CHAR_ALIAS.put("0-9a-zA-Z", ALPHA_NUMERIC);
        CHAR_ALIAS.put("0-9!", NUMERIC_SPECIAL);
        CHAR_ALIAS.put("0-9!:w", NUMERIC_SPECIAL);
        CHAR_ALIAS.put("a-z!", LOWER_SPECIAL);
        CHAR_ALIAS.put("a-z!:w", LOWER_SPECIAL);
        CHAR_ALIAS.put("A-Z!", UPPER_SPECIAL);
        CHAR_ALIAS.put("A-Z!:w", UPPER_SPECIAL);
        CHAR_ALIAS.put("a-zA-Z!", ALPHA_SPECIAL);
        CHAR_ALIAS.put("a-zA-Z!:w", ALPHA_SPECIAL);
        CHAR_ALIAS.put("0-9a-z!", LOWER_NUMERIC_SPECIAL);
        CHAR_ALIAS.put("0-9a-z!:w", LOWER_NUMERIC_SPECIAL);
        CHAR_ALIAS.put("0-9A-Z!", UPPER_NUMERIC_SPECIAL);
        CHAR_ALIAS.put("0-9A-Z!:w", UPPER_NUMERIC_SPECIAL);
        CHAR_ALIAS.put("0-9a-zA-Z!", VISIBLE);
        CHAR_ALIAS.put("0-9a-zA-Z!:w", VISIBLE);
        CHAR_ALIAS.put("0-9:w", NUMERIC_SPACE);
        CHAR_ALIAS.put("a-z:w", LOWER_SPACE);
        CHAR_ALIAS.put("A-Z:w", UPPER_SPACE);
        CHAR_ALIAS.put("a-zA-Z:w", ALPHA_SPACE);
        CHAR_ALIAS.put("0-9a-z:w", LOWER_NUMERIC_SPACE);
        CHAR_ALIAS.put("0-9A-Z:w", UPPER_NUMERIC_SPACE);
        CHAR_ALIAS.put("0-9a-zA-Z:w", ALPHA_NUMERIC_SPACE);
    }

    private String chars;
    private int minLen, maxLen;
    private long first, last;

    private long current = -1;
    private int currentLen;
    private long currentLenLast;
    // private boolean asc = true;

    double total;

    public CharPattern(String chars, int minLen, int maxLen, String first, String last, String prefix, String suffix) {
        super(prefix, suffix);
        init(chars, minLen, maxLen, first, last);
    }

    public CharPattern(String chars, int minLen, int maxLen, String first, String last, String[] prefix, String[] suffix) {
        super(prefix, suffix);
        init(chars, minLen, maxLen, first, last);
    }

    public CharPattern(String chars, int len, long first, long last, String[] prefix, String[] suffix) {
        super(prefix, suffix);
        this.minLen = this.maxLen = len;
        this.chars = chars;
        this.first = first;
        this.last = last;
        this.total = last - first + 1;
    }

    private void init(String chars, int minLen, int maxLen, String first, String last) {
        this.chars = chars;

        this.minLen = first == null || first.length() == 0 ? minLen : first.length();
        this.maxLen = last == null || last.length() == 0 ? maxLen : last.length();
        this.minLen = Math.max(1, Math.min(this.minLen, this.maxLen));
        this.maxLen = Math.max(1, Math.max(this.minLen, this.maxLen));
        this.first = first == null || first.length() == 0 ? 0 : strToNum(first);
        this.last = last == null || last.length() == 0 ? getTotalCombinations(this.maxLen) - 1 : strToNum(last);
        for (int i = this.minLen; i <= this.maxLen; i++) {
            if (i == this.minLen) {
                this.total += getTotalCombinations(i) - this.first;
            }
            else {
                this.total += getTotalCombinations(i);
            }

            if (i == this.maxLen) {
                this.total -= getTotalCombinations(i) - (this.last + 1);
            }
        }
    }

    public String getChars() {
        return chars;
    }

    @Override
    public String prefixSuffixNext() {
        if (!prefixSuffixHasNext()) {
            return null;
        }

        if (currentLen == 0) {
            currentLen = minLen;
            current = first;
            currentLenLast = getTotalCombinations(currentLen) - 1;
        }
        else if (current >= currentLenLast) {
            ++currentLen;
            current = 0;
            currentLenLast = getTotalCombinations(currentLen) - 1;
        }
        else {
            ++current;
        }

        return padLeft(numToStr(current), chars.charAt(0), currentLen);
    }

    @Override
    public boolean prefixSuffixHasNext() {
        return currentLen < maxLen || (currentLen == maxLen && current < last);
    }

    @Override
    public double prefixSuffixTotal() {
        return total;
    }

    private String padLeft(String s, char c, int len) {
        while (s.length() < len) {
            s = c + s;
        }

        return s;
    }

    private int strToNum(String s) {
        int n = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            n += chars.indexOf(s.charAt(i)) * Math.pow(chars.length(), s.length() - i - 1);
        }

        return n;
    }

    private String numToStr(long n) {
        long on = n;
        try {
            String s = "";
            // find number of "digit"
            long i = 0;
            while (n >= Math.pow(chars.length(), ++i)) {
            }
            --i;
            while (i > 0) {
                long p = (long) Math.pow(chars.length(), i);
                s += chars.charAt((int) (n / p));
                n = n % p;
                i--;
            }

            s += chars.charAt((int) n);
            return s;
        }
        catch (Exception ex) {
            System.out.println(on + "," + currentLen + "," + maxLen + "," + currentLenLast);
            ex.printStackTrace();
            return "";
        }
    }

    public long getTotalCombinations(int len) {
        return (long) Math.pow(this.chars.length(), len);
    }

    public long getFirstSerial(int len) {
        if (len == this.minLen) {
            return first;
        }
        else {
            return 0;
        }
    }

    public long getLastSerial(int len) {
        if (len == this.maxLen) {
            return last;
        }
        else {
            return getTotalCombinations(len) - 1;
        }
    }

    public static String getCharCmdlinePattern(boolean numeric, boolean lower, boolean upper, boolean special,
            boolean space, String custom) {

        String charPattern = "";
        if (numeric) {
            charPattern += CharPattern.ALIAS_NUMERIC;
        }
        if (lower) {
            charPattern += CharPattern.ALIAS_LOWER;
        }
        if (upper) {
            charPattern += CharPattern.ALIAS_UPPER;
        }
        if (special) {
            charPattern += CharPattern.ALIAS_SPECIAL;
        }
        if (space) {
            charPattern += CharPattern.ALIAS_SPACE;
        }

        String pattern = CharPattern.CHAR_ALIAS.get(charPattern);
        if (pattern == null) {
            charPattern = "\"Invalid Pattern {" + charPattern + "}\"";
        }

        return charPattern;
    }

    public static void main(String[] args) {
        testAlias();

        System.out.println();
        testCombinations(NUMERIC, 4);
        System.out.println();
        testCombinations(NUMERIC, 6);
        System.out.println();
        testCombinations(LOWER, 4);
        System.out.println();
        testCombinations(UPPER, 4);
        System.out.println();
        testCombinations(ALPHA_NUMERIC, 3);
        System.out.println();
        testCombinations(VISIBLE, 3);
        System.out.println();
        testCombinations("abc", 12);

        System.out.println();
        testPerformance(LOWER, 8);
        System.out.println();
    }

    private static void testAlias() {
        // Load alias name
        Map<String, String> aliasValueMap = new HashMap<String, String>();
        Field[] fields = CharPattern.class.getFields();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getType().equals(String.class)) {
                try {
                    String s = fields[i].get(null).toString();
                    if (aliasValueMap.get(s) == null) {
                        aliasValueMap.put(s, fields[i].getName());
                    }
                }
                catch (Exception ex) {
                    ;
                }
            }
        }

        System.out.println("Test command line pattern combinations");

        System.out.println("0 a z ! _");
        System.out.println("---------");

        for (int f1 = 1; f1 >= 0; f1--) {
            for (int f2 = 1; f2 >= 0; f2--) {
                for (int f3 = 1; f3 >= 0; f3--) {
                    for (int f4 = 1; f4 >= 0; f4--) {
                        for (int f5 = 1; f5 >= 0; f5--) {
                            String cp = getCharCmdlinePattern(f1 > 0, f2 > 0, f3 > 0, f4 > 0, f5 > 0, null);
                            String pattern = CHAR_ALIAS.get(cp);
                            if (pattern != null) {
                                pattern = aliasValueMap.get(pattern);
                            }
                            System.out.println(f1 + " " + f2 + " " + f3 + " " + f4 + " " + f5 + " = " + cp
                                    + (pattern != null ? " (" + pattern + ")" : ""));
                        }
                    }
                }
            }
        }
    }

    private static void testCombinations(String p, int len) {
        long expected = (long) Math.pow(p.length(), len);
        System.out.println("Test combinations");
        CharPattern cp = new CharPattern(p, len, len, null, null, "", "");
        System.out.println("Length=" + len + ", Chars=" + p + ", " + expected + " combinations");

        Set<String> set = new HashSet<String>();
        char prevChar = 0;
        int totalSize = 0;
        while (cp.hasNext()) {
            String s = cp.next();
            if (s.length() != len) {
                System.out.println("Error " + s + "    " + p + " " + len);
            }
            if (prevChar == 0) {
                prevChar = s.charAt(0);
            }
            // if (expected >= 1000000 && prevChar != s.charAt(0)) {
            // System.out.println(set.size() + " " + set.iterator().next().charAt(0) + " test OK");
            // totalSize += set.size();
            // prevChar = s.charAt(0);
            // set.clear();
            // }
            set.add(s);
        }

        totalSize += set.size();
        if (totalSize == expected) {
            System.out.println("PASSED");
        }
        else {
            System.out.println("Error length " + p + " " + len);
        }
    }

    private static void testPerformance(String p, int len) {
        System.out.println("Test Performance");
        final CharPattern cp = new CharPattern(p, len, len, null, null, "", "");
        System.out.println("Length=" + len + ", Chars=" + cp.getChars() + ", " + cp.total + " combinations");

        final Performance perf = new Performance((long) cp.total(), 10000);
        final boolean[] stop = { false };
        Thread thread = new Thread(new Runnable() {
            public void run() {
                int n = 0;
                while (!stop[0] && cp.hasNext()) {
                    cp.next();
                    ++n;
                    if (perf.addSample(n)) {
                        System.out.println(perf.getResultDescWithAvg());
                    }
                }
                perf.addSample(n);
                System.out.println(perf.getResultDescWithAvg());
                System.out.println(n);
            }
        });

        thread.start();

        synchronized (thread) {
            while (thread.isAlive()) {
                try {
                    thread.wait(1000);
                    if (perf.getEllapsed() > 35000) {
                        stop[0] = true;
                    }
                }
                catch (InterruptedException ex) {
                    ;
                }
            }
        }
    }
}
