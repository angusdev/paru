package org.ellab.paru.pattern;

import org.testng.Assert;
import org.testng.annotations.Test;

public class PrefixSuffixPatternTest {
    private static final String[] PATTERN_SINGLE = { "abc" };
    private static final String[] PATTERN = { "abc", "defg", "z", "123" };

    private class DummyPattern extends PrefixSuffixPattern {
        private String[] pattern;
        private int curr = -1;
        private boolean alwaysHasNext;

        private DummyPattern(String prefix, String suffix, String[] pattern, boolean alwaysHasNext) {
            super(prefix, suffix);
            this.pattern = pattern;
            this.alwaysHasNext = alwaysHasNext;
        }

        @Override
        protected String prefixSuffixNext() {
            return curr + 1 < pattern.length ? pattern[++curr] : null;
        }

        @Override
        protected boolean prefixSuffixHasNext() {
            return alwaysHasNext || curr + 1 < pattern.length;
        }

        @Override
        protected double prefixSuffixTotal() {
            return pattern.length;
        }
    }

    @Test
    public void testNoPrefixSuffixBasic() {
        DummyPattern p = new DummyPattern("1,2,3", "a,b", PATTERN_SINGLE, false);
        Assert.assertEquals(p.total(), 6d);
        Assert.assertEquals(p.getPrefix().length, 3);
        Assert.assertEquals(p.getSuffix().length, 2);
        while (p.hasNext()) {
            Assert.assertNotNull(p.next());
        }
        // not hasNext() and call next() will return null
        Assert.assertNull(p.next());
        Assert.assertNull(p.prefixSuffixNext());
    }

    @Test
    public void testNoPrefixSuffixSingle() {
        DummyPattern p = new DummyPattern(null, null, PATTERN_SINGLE, false);
        Assert.assertEquals((int) p.total(), 1);
        Assert.assertTrue(p.hasNext());
        Assert.assertEquals(p.next(), PATTERN_SINGLE[0]);
        Assert.assertFalse(p.hasNext());
        Assert.assertNull(p.next());
        Assert.assertNull(p.next());
    }

    @Test
    public void testNoPrefixSuffix() {
        DummyPattern p = new DummyPattern(null, null, PATTERN, false);
        Assert.assertEquals((int) p.total(), PATTERN.length);
        for (int i = 0; i < PATTERN.length; i++) {
            Assert.assertTrue(p.hasNext());
            Assert.assertEquals(p.next(), PATTERN[i]);
        }
        Assert.assertFalse(p.hasNext());
        Assert.assertNull(p.next());
        Assert.assertNull(p.next());
    }

    @Test
    public void testSinglePrefixSinglePattern() {
        DummyPattern p = new DummyPattern("a", null, PATTERN_SINGLE, false);
        Assert.assertEquals((int) p.total(), 1);
        Assert.assertTrue(p.hasNext());
        Assert.assertEquals(p.next(), "a" + PATTERN_SINGLE[0]);
        Assert.assertFalse(p.hasNext());
        Assert.assertNull(p.next());
        Assert.assertNull(p.next());
    }

    @Test
    public void testEmptySinglePrefixSinglePattern() {
        DummyPattern p = new DummyPattern(",a", null, PATTERN_SINGLE, false);
        Assert.assertEquals((int) p.total(), 2);
        Assert.assertTrue(p.hasNext());
        Assert.assertEquals(p.next(), PATTERN_SINGLE[0]);
        Assert.assertTrue(p.hasNext());
        Assert.assertEquals(p.next(), "a" + PATTERN_SINGLE[0]);
        Assert.assertFalse(p.hasNext());
        Assert.assertNull(p.next());
        Assert.assertNull(p.next());
    }

    @Test
    public void testMultiplePrefixPattern() {
        DummyPattern p = new DummyPattern(",A,B", null, PATTERN, false);
        Assert.assertEquals((int) p.total(), 3 * PATTERN.length);
        for (int i = 0; i < PATTERN.length; i++) {
            Assert.assertTrue(p.hasNext());
            Assert.assertEquals(p.next(), PATTERN[i]);
            Assert.assertTrue(p.hasNext());
            Assert.assertEquals(p.next(), "A" + PATTERN[i]);
            Assert.assertTrue(p.hasNext());
            Assert.assertEquals(p.next(), "B" + PATTERN[i]);
        }
        Assert.assertFalse(p.hasNext());
        Assert.assertNull(p.next());
        Assert.assertNull(p.next());
    }

    @Test
    public void testMultiplePrefixSuffixPattern() {
        DummyPattern p = new DummyPattern(",A,B", "X,,Y,Z", PATTERN, false);
        Assert.assertEquals((int) p.total(), 3 * 4 * PATTERN.length);
        for (int i = 0; i < PATTERN.length; i++) {
            Assert.assertTrue(p.hasNext());
            Assert.assertEquals(p.next(), PATTERN[i] + "X");
            Assert.assertEquals(p.next(), PATTERN[i]);
            Assert.assertEquals(p.next(), PATTERN[i] + "Y");
            Assert.assertEquals(p.next(), PATTERN[i] + "Z");
            Assert.assertEquals(p.next(), "A" + PATTERN[i] + "X");
            Assert.assertEquals(p.next(), "A" + PATTERN[i]);
            Assert.assertEquals(p.next(), "A" + PATTERN[i] + "Y");
            Assert.assertEquals(p.next(), "A" + PATTERN[i] + "Z");
            Assert.assertEquals(p.next(), "B" + PATTERN[i] + "X");
            Assert.assertEquals(p.next(), "B" + PATTERN[i]);
            Assert.assertEquals(p.next(), "B" + PATTERN[i] + "Y");
            Assert.assertEquals(p.next(), "B" + PATTERN[i] + "Z");
        }
        Assert.assertFalse(p.hasNext());
        Assert.assertNull(p.next());
        Assert.assertNull(p.next());
    }

    @Test
    public void testNoPrefixSuffixAlwaysHasNext() {
        DummyPattern p = new DummyPattern("1,2,3", "a,b", PATTERN_SINGLE, true);
        Assert.assertEquals((int) p.total(), 6);
        Assert.assertEquals(p.getPrefix().length, 3);
        Assert.assertEquals(p.getSuffix().length, 2);
        Assert.assertTrue(p.hasNext());
        Assert.assertEquals("1abca", p.next());
        Assert.assertEquals("1abcb", p.next());
        Assert.assertEquals("2abca", p.next());
        Assert.assertEquals("2abcb", p.next());
        Assert.assertEquals("3abca", p.next());
        Assert.assertEquals("3abcb", p.next());
        Assert.assertTrue(p.hasNext());
        Assert.assertNull(p.next());
        Assert.assertNull(p.prefixSuffixNext());
    }

}
