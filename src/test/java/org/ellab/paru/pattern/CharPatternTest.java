package org.ellab.paru.pattern;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CharPatternTest {
    private void assertLast(CharPattern cp, String expected) {
        String value = null;
        while (cp.hasNext()) {
            value = cp.next();
        }
        Assert.assertEquals(value, expected);
    }

    @Test
    public void testSimple() {
        CharPattern cp = new CharPattern("0a!", 1, 3, null, null, "", "");
        Assert.assertEquals(cp.total(), 39d);
        Assert.assertEquals(cp.next(), "0");
        Assert.assertEquals(cp.next(), "a");
        Assert.assertEquals(cp.next(), "!");
        Assert.assertEquals(cp.next(), "00");
        Assert.assertEquals(cp.next(), "0a");
        Assert.assertEquals(cp.next(), "0!");
        Assert.assertEquals(cp.next(), "a0");
        Assert.assertEquals(cp.next(), "aa");
        Assert.assertEquals(cp.next(), "a!");
        Assert.assertEquals(cp.next(), "!0");
        Assert.assertEquals(cp.next(), "!a");
        Assert.assertEquals(cp.next(), "!!");
        Assert.assertEquals(cp.next(), "000");
        Assert.assertEquals(cp.next(), "00a");
        Assert.assertEquals(cp.next(), "00!");
        Assert.assertEquals(cp.next(), "0a0");
        Assert.assertEquals(cp.next(), "0aa");
        Assert.assertEquals(cp.next(), "0a!");
        Assert.assertEquals(cp.next(), "0!0");
        Assert.assertEquals(cp.next(), "0!a");
        Assert.assertEquals(cp.next(), "0!!");
        Assert.assertEquals(cp.next(), "a00");
        Assert.assertEquals(cp.next(), "a0a");
        Assert.assertEquals(cp.next(), "a0!");
        Assert.assertEquals(cp.next(), "aa0");
        Assert.assertEquals(cp.next(), "aaa");
        Assert.assertEquals(cp.next(), "aa!");
        Assert.assertEquals(cp.next(), "a!0");
        Assert.assertEquals(cp.next(), "a!a");
        Assert.assertEquals(cp.next(), "a!!");
        Assert.assertEquals(cp.next(), "!00");
        Assert.assertEquals(cp.next(), "!0a");
        Assert.assertEquals(cp.next(), "!0!");
        Assert.assertEquals(cp.next(), "!a0");
        Assert.assertEquals(cp.next(), "!aa");
        Assert.assertEquals(cp.next(), "!a!");
        Assert.assertEquals(cp.next(), "!!0");
        Assert.assertEquals(cp.next(), "!!a");
        Assert.assertEquals(cp.next(), "!!!");
        Assert.assertFalse(cp.hasNext());
    }

    @Test
    public void testFirst1() {
        CharPattern cp = new CharPattern("0123456789", 3, 3, "000", null, "", "");
        Assert.assertEquals(cp.total, 1000d);
        Assert.assertEquals(cp.next(), "000");
        Assert.assertEquals(cp.next(), "001");
        Assert.assertEquals(cp.next(), "002");
        assertLast(cp, "999");
    }

    @Test
    public void testFirst2() {
        CharPattern cp = new CharPattern("0123456789", 3, 3, "438", null, "", "");
        Assert.assertEquals(cp.total, 1000 - 438d);
        Assert.assertEquals(cp.next(), "438");
        Assert.assertEquals(cp.next(), "439");
        Assert.assertEquals(cp.next(), "440");
        Assert.assertEquals(cp.next(), "441");
    }

    @Test
    public void testFirst3() {
        CharPattern cp = new CharPattern("abcdefghijk", 3, 3, "dej", null, "", "");
        Assert.assertEquals(cp.next(), "dej");
        Assert.assertEquals(cp.next(), "dek");
        Assert.assertEquals(cp.next(), "dfa");
        Assert.assertEquals(cp.next(), "dfb");
    }

    @Test
    public void testLast1() {
        CharPattern cp = new CharPattern("0123456789", 3, 3, null, "000", "", "");
        Assert.assertEquals(cp.total, 1d);
        Assert.assertEquals(cp.next(), "000");
        Assert.assertFalse(cp.hasNext());
    }

    @Test
    public void testLast2() {
        CharPattern cp = new CharPattern("0123456789", 3, 3, null, "759", "", "");
        Assert.assertEquals(cp.total, 760d);
        Assert.assertEquals(cp.next(), "000");
        assertLast(cp, "759");
    }

    @Test
    public void testLast3() {
        CharPattern cp = new CharPattern("0123456789", 1, 4, null, "759", "", "");
        Assert.assertEquals(cp.next(), "0");
        assertLast(cp, "759");
    }

    @Test
    public void testLast4() {
        CharPattern cp = new CharPattern("abcdefghijk", 3, 3, null, "jak", "", "");
        Assert.assertEquals(cp.next(), "aaa");
        assertLast(cp, "jak");
    }

    @Test
    public void testLast5() {
        CharPattern cp = new CharPattern("abcdefghijk", 1, 4, null, "jak", "", "");
        Assert.assertEquals(cp.next(), "a");
        assertLast(cp, "jak");
    }

    @Test
    public void testFirstLast1() {
        CharPattern cp = new CharPattern("0123456789", 3, 3, "223", "225", "", "");
        Assert.assertEquals(cp.total, 3d);
        Assert.assertEquals(cp.next(), "223");
        assertLast(cp, "225");
    }

    @Test
    public void testFirstLast2() {
        CharPattern cp = new CharPattern("0123456789", 2, 3, "99", "101", "", "");
        Assert.assertEquals(cp.total, 1d + 102);
        Assert.assertEquals(cp.next(), "99");
        Assert.assertEquals(cp.next(), "000");
        Assert.assertEquals(cp.next(), "001");
        assertLast(cp, "101");
    }

    @Test
    public void testFirstLast3() {
        CharPattern cp = new CharPattern("0123456789", 2, 3, "23", "223", "", "");
        // 23-00, 000-099, 100-199, 200-223
        Assert.assertEquals(cp.total, 100d - 23 + 100 + 100 + 24);
        Assert.assertEquals(cp.next(), "23");
        assertLast(cp, "223");
    }

    @Test
    public void testFirstLast4() {
        CharPattern cp = new CharPattern("abcdefghijk", 2, 4, "gk", "dkfc", "", "");
        Assert.assertEquals(cp.next(), "gk");
        assertLast(cp, "dkfc");
    }
}
