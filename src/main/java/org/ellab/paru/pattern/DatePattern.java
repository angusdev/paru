package org.ellab.paru.pattern;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePattern extends PrefixSuffixPattern {
    private DateFormat datefmt;
    private DateFormat yyyymmdd = new SimpleDateFormat("yyyyMMdd");

    private Calendar current;
    private Date first, last;
    private boolean asc;

    double total;

    public DatePattern(String format, String first, String last, String prefix, String suffix) throws ParseException {
        super(prefix, suffix);

        datefmt = new SimpleDateFormat(format);

        // remove hour/min/sec
        Date now = yyyymmdd.parse(yyyymmdd.format(new Date()));
        if (first.matches("^\\d{1,7}$")) {
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(now);
            cal.add(GregorianCalendar.DATE, -Integer.valueOf(first));
            this.first = cal.getTime();
        }
        else {
            this.first = yyyymmdd.parse(first);
        }
        if (last.matches("^\\d{1,7}$")) {
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(now);
            cal.add(GregorianCalendar.DATE, -Integer.valueOf(last));
            this.last = cal.getTime();
        }
        else {
            this.last = yyyymmdd.parse(last);
        }
        this.asc = this.last.getTime() > this.first.getTime();
        this.total = (int) Math.ceil(Math.abs(this.last.getTime() - this.first.getTime()) / 86400000) + 1;
    }

    @Override
    public boolean prefixSuffixHasNext() {
        if (current == null) {
            return true;
        }
        Date thisDate = current.getTime();
        return !thisDate.equals(last) || (asc && thisDate.before(last)) || (!asc && thisDate.after(last));
    }

    @Override
    public String prefixSuffixNext() {
        if (current == null) {
            current = GregorianCalendar.getInstance();
            current.setTime(first);
        }
        else {
            current.add(GregorianCalendar.DATE, asc ? 1 : -1);
        }

        Date thisDate = current.getTime();
        if ((asc && thisDate.after(last)) || (!asc && thisDate.before(last))) {
            return null;
        }
        else {
            return datefmt.format(thisDate);
        }
    }

    @Override
    public double prefixSuffixTotal() {
        return total;
    }
}
