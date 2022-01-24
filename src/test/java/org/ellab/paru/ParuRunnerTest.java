package org.ellab.paru;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ellab.paru.pattern.PasswordPattern;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ParuRunnerTest {
    private static class TestParaListener implements Paru.ParuListener {
        private ParuRunner pr;
        private long expected;
        private boolean expectedResult;
        private Map<Object, Object> failEquals = Collections.synchronizedMap(new HashMap<Object, Object>());
        private List<String> fail = Collections.synchronizedList(new ArrayList<String>());
        private Set<String> patterns = Collections.synchronizedSet(new HashSet<String>());
        private String result = "";

        private TestParaListener(long expected, boolean expectedResult) {
            this.expected = expected;
            this.expectedResult = expectedResult;
        }

        public void setPr(ParuRunner pr) {
            this.pr = pr;
        }

        @Override
        public void result(String result) {
            if (!expectedResult) {
                fail.add("Shouldn't have result");
            }
            this.result = result;
        }

        @Override
        public void progress(Performance perf, String current) {
            patterns.add(current);
        }

        @Override
        public void noresult() {
            result = null;
            if (expectedResult) {
                fail.add("Should have result");
            }
            else {
                if (patterns.size() != expected) {
                    failEquals.put(patterns.size(), expected);
                }
            }
        }

        @Override
        public void error(String s, Exception ex) {
            if (pr != null) {
                pr.stop();
            }
            if (ex != null) {
                ex.printStackTrace();
            }
            fail.add(s + (ex != null ? (" " + ex.getMessage()) : ""));
        }

        @Override
        public void combinations(long combinations) {
            if (combinations != expected) {
                failEquals.put(combinations, expected);
            }
        }

        public void assertTest() {
            for (Map.Entry<Object, Object> e : failEquals.entrySet()) {
                Assert.assertEquals(e.getKey(), e.getValue());
            }

            for (String f : fail) {
                Assert.fail(f);
            }
        }
    }

    private void testFileHandlerExceptionsHelper(String filename, String expected) {
        TestParaListener listener = new TestParaListener(1, false);
        try {
            ParuRunner.createFromCharPattern("src/test/resources/" + filename, listener, 0, "0", 1, 1, null, null, "",
                    "");
            Assert.fail("Should throw IOException");
        }
        catch (IOException ex) {
            Assert.assertEquals(1, listener.fail.size());
            Assert.assertEquals(listener.fail.iterator().next(), expected);
        }
    }

    @Test
    public void testFileHandlerExceptions() {
        testFileHandlerExceptionsHelper("null.zip", Constants.MSG_FILE_NOT_FOUND);
        testFileHandlerExceptionsHelper("null.pdf", Constants.MSG_FILE_NOT_FOUND);
        testFileHandlerExceptionsHelper("null.xls", Constants.MSG_FILE_NOT_FOUND);
        testFileHandlerExceptionsHelper("null.xlsx", Constants.MSG_FILE_NOT_FOUND);
        testFileHandlerExceptionsHelper("invalid-format.zip", Constants.MSG_INVALID_FILE_FORMAT);
        testFileHandlerExceptionsHelper("invalid-format.pdf", Constants.MSG_INVALID_FILE_FORMAT);
        testFileHandlerExceptionsHelper("invalid-format.xls", Constants.MSG_INVALID_FILE_FORMAT);
        testFileHandlerExceptionsHelper("invalid-format.xlsx", Constants.MSG_INVALID_FILE_FORMAT);
        testFileHandlerExceptionsHelper("not-encrypted.zip", Constants.MSG_FILE_NOT_ENCRYPTED);
        testFileHandlerExceptionsHelper("not-encrypted.pdf", Constants.MSG_FILE_NOT_ENCRYPTED);
        testFileHandlerExceptionsHelper("not-encrypted.xls", Constants.MSG_FILE_NOT_ENCRYPTED);
        testFileHandlerExceptionsHelper("not-encrypted.xlsx", Constants.MSG_FILE_NOT_ENCRYPTED);
        testFileHandlerExceptionsHelper("foo.txt", Constants.MSG_INVALID_FILE_FORMAT);
    }

    private void testSimpleHelper(String ext) throws IOException, InterruptedException {
        TestParaListener listener = new TestParaListener(10, true);
//        ParuRunner pr = ParuRunner.createFromCharPattern("src/test/resources/2311." + ext, listener, 0, "0123456789",
//                3, 3, null, null, "", "1,2,3,0");
        ParuRunner pr = ParuRunner.createFromCharPattern("src/test/resources/2311." + ext, listener, 0, "0123456789",
                1, 1, null, null, "231", null);
        listener.setPr(pr);
        listener.assertTest();
        pr.start();
        while (pr.isRunning()) {
            Thread.sleep(50);
        }
        listener.assertTest();
        Assert.assertEquals(listener.result, "2311");
    }

    @Test
    public void testSimpleZip() throws IOException, InterruptedException {
        testSimpleHelper("zip");
    }

    @Test
    public void testSimplePdf() throws IOException, InterruptedException {
        testSimpleHelper("pdf");
    }

    @Test
    public void testSimpleXls() throws IOException, InterruptedException {
        testSimpleHelper("xls");
    }

    @Test
    public void testSimpleXlsx() throws IOException, InterruptedException {
        testSimpleHelper("xlsx");
    }

    @Test
    public void testDatePatternSingle() throws IOException, InterruptedException {
        TestParaListener listener = new TestParaListener(10, false);
        ParuRunner pr = ParuRunner.create("src/test/resources/2311.zip", listener, 0, new PasswordPattern() {
            private int i = 0;

            @Override
            public double total() {
                return 10;
            }

            @Override
            public String next() {
                return "" + (i++);
            }

            @Override
            public boolean hasNext() {
                return i < 10;
            }
        });
        listener.setPr(pr);
        listener.assertTest();
        pr.start();
        while (pr.isRunning()) {
            Thread.sleep(50);
        }
        listener.assertTest();
    }

    @Test
    public void testCharPatternSimple() throws IOException, InterruptedException {
        TestParaListener listener = new TestParaListener(1000, false);
        ParuRunner pr = ParuRunner.createFromCharPattern("src/test/resources/2311.zip", listener, 0, "0123456789", 3,
                3, null, null, "", "");
        listener.setPr(pr);
        listener.assertTest();
        pr.start();
        Thread.sleep(50);
        pr.pause();
        Thread.sleep(50);
        pr.resume();
        while (pr.isRunning()) {
            Thread.sleep(50);
        }
        listener.assertTest();
    }

    @Test
    public void testCharPatternMultiLen() throws IOException, InterruptedException {
        TestParaListener listener = new TestParaListener(1000 + 100 + 10, false);
        ParuRunner pr = ParuRunner.createFromCharPattern("src/test/resources/2311.zip", listener, 0, "0123456789", 1,
                3, null, null, "", "");
        listener.setPr(pr);
        listener.assertTest();
        pr.start();
        while (pr.isRunning()) {
            Thread.sleep(50);
        }
        listener.assertTest();
    }

    @Test
    public void testCharPatternMultiLenFirstLast() throws IOException, InterruptedException {
        TestParaListener listener = new TestParaListener(778 + 100 + 10 - 7, false);
        ParuRunner pr = ParuRunner.createFromCharPattern("src/test/resources/2311.zip", listener, 0, "0123456789", 1,
                3, "7", "777", "", "");
        listener.setPr(pr);
        listener.assertTest();
        pr.start();
        while (pr.isRunning()) {
            Thread.sleep(50);
        }
        listener.assertTest();
        Assert.assertNull(listener.result, "Shouldn't have result");
    }

    @Test
    public void testCharPatternMultiLenHasResult() throws IOException, InterruptedException {
        TestParaListener listener = new TestParaListener(10000 + 1000 + 100 + 10, true);
        ParuRunner pr = ParuRunner.createFromCharPattern("src/test/resources/2311.zip", listener, 0, "0123456789", 1,
                4, null, null, "", "");
        listener.setPr(pr);
        listener.assertTest();
        pr.start();
        while (pr.isRunning()) {
            Thread.sleep(50);
        }
        listener.assertTest();
        Assert.assertEquals(listener.result, "2311");
    }

    @Test
    public void testCharPatternMultiLenPrefixHasResult() throws IOException, InterruptedException {
        TestParaListener listener = new TestParaListener(10000 + 1000 + 100 + 10, true);
        ParuRunner pr = ParuRunner.createFromCharPattern("src/test/resources/2311.zip", listener, 0, "0123456789", 1,
                4, null, null, "2", "");
        listener.setPr(pr);
        listener.assertTest();
        pr.start();
        while (pr.isRunning()) {
            Thread.sleep(50);
        }
        listener.assertTest();
        Assert.assertEquals(listener.result, "2311");
    }

    @Test
    public void testCharPatternMultiPrefixHasResult() throws IOException, InterruptedException {
        TestParaListener listener = new TestParaListener(1000 * 3, true);
        ParuRunner pr = ParuRunner.createFromCharPattern("src/test/resources/2311.zip", listener, 0, "0123456789", 3,
                3, null, null, "1,2,3", "");
        listener.setPr(pr);
        listener.assertTest();
        pr.start();
        while (pr.isRunning()) {
            Thread.sleep(50);
        }
        listener.assertTest();
        Assert.assertEquals(listener.result, "2311");
    }

    @Test
    public void testCharPatternMultiSuffoxHasResult() throws IOException, InterruptedException {
        TestParaListener listener = new TestParaListener(1000 * 4, true);
        ParuRunner pr = ParuRunner.createFromCharPattern("src/test/resources/2311.zip", listener, 0, "0123456789", 3,
                3, null, null, "", "1,2,3,0");
        listener.setPr(pr);
        listener.assertTest();
        pr.start();
        while (pr.isRunning()) {
            Thread.sleep(50);
        }
        listener.assertTest();
        Assert.assertEquals(listener.result, "2311");
    }

}
