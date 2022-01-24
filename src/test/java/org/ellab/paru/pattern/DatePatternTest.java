package org.ellab.paru.pattern;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.testng.Assert;
import org.testng.annotations.Test;

public class DatePatternTest {

    @Test
    public void testAsc() throws ParseException {
        DatePattern dp = new DatePattern("yyyyMMdd", "20170227", "20170302", null, null);
        Assert.assertEquals(dp.total(), 4d);
        Assert.assertEquals(dp.next(), "20170227");
        Assert.assertEquals(dp.next(), "20170228");
        Assert.assertEquals(dp.next(), "20170301");
        Assert.assertEquals(dp.next(), "20170302");
        Assert.assertFalse(dp.hasNext());
        Assert.assertNull(dp.next());
        Assert.assertNull(dp.prefixSuffixNext());
    }

    @Test
    public void testDsc() throws ParseException {
        DatePattern dp = new DatePattern("yyyyMMdd", "20170104", "20161227", null, null);
        Assert.assertEquals(dp.total(), 9d);
        Assert.assertEquals(dp.next(), "20170104");
        Assert.assertEquals(dp.next(), "20170103");
        Assert.assertEquals(dp.next(), "20170102");
        Assert.assertEquals(dp.next(), "20170101");
        Assert.assertEquals(dp.next(), "20161231");
        Assert.assertEquals(dp.next(), "20161230");
        Assert.assertEquals(dp.next(), "20161229");
        Assert.assertEquals(dp.next(), "20161228");
        Assert.assertEquals(dp.next(), "20161227");
        Assert.assertFalse(dp.hasNext());
        Assert.assertNull(dp.next());
        Assert.assertNull(dp.prefixSuffixNext());
    }

    @Test
    public void testRange() throws ParseException {
        DateFormat yyyymmdd = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(new Date());

        DatePattern dp = new DatePattern("yyyyMMdd", "3", "7", null, null);
        Assert.assertEquals(dp.total(), 5d);
        cal.add(GregorianCalendar.DATE, -3);
        Assert.assertEquals(dp.next(), yyyymmdd.format(cal.getTime()));
        cal.add(GregorianCalendar.DATE, -1);
        Assert.assertEquals(dp.next(), yyyymmdd.format(cal.getTime()));
        cal.add(GregorianCalendar.DATE, -1);
        Assert.assertEquals(dp.next(), yyyymmdd.format(cal.getTime()));
        cal.add(GregorianCalendar.DATE, -1);
        Assert.assertEquals(dp.next(), yyyymmdd.format(cal.getTime()));
        cal.add(GregorianCalendar.DATE, -1);
        Assert.assertEquals(dp.next(), yyyymmdd.format(cal.getTime()));
        Assert.assertFalse(dp.hasNext());
        Assert.assertNull(dp.next());
        Assert.assertNull(dp.prefixSuffixNext());
    }

    @Test
    public void testTotalLeapYear() throws ParseException {
        DatePattern dp = new DatePattern("yyyyMMdd", "20160227", "20160301", null, null);
        Assert.assertEquals(dp.total(), 4d);
        dp = new DatePattern("yyyyMMdd", "20150227", "20160301", null, null);
        Assert.assertEquals(dp.total(), 365d + 4);
        dp = new DatePattern("yyyyMMdd", "19991231", "20010301", null, null);
        Assert.assertEquals(dp.total(), 1d + 366 + 31 + 28 + 1);
    }
}
