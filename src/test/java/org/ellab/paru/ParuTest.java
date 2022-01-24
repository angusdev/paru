package org.ellab.paru;

import java.security.Permission;
import java.text.ParseException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ParuTest {
    protected static class ExitException extends SecurityException {
        private static final long serialVersionUID = 1L;

        public final int status;

        public ExitException(int status) {
            super("There is no escape!");
            this.status = status;
        }
    }

    private static class NoExitSecurityManager extends SecurityManager {
        @Override
        public void checkPermission(Permission perm) {
            // allow anything.
        }

        @Override
        public void checkPermission(Permission perm, Object context) {
            // allow anything.
        }

        @Override
        public void checkExit(int status) {
            super.checkExit(status);
            throw new ExitException(status);
        }
    }

    @BeforeClass
    public void beforeClass() throws Exception {
        System.setSecurityManager(new NoExitSecurityManager());
    }

    @AfterClass
    protected void afterClass() throws Exception {
        System.setSecurityManager(null);
    }

    @Test(expectedExceptions = { ExitException.class })
    public void testShowUsage() throws Exception {
        Paru.main(new String[] { "src/test/resources/2311.zip" });
    }

    @Test(expectedExceptions = { ParseException.class })
    public void testInvalidPattern() throws Exception {
        Paru.main(new String[] { "src/test/resources/2311.zip", "y" });
    }

    @Test
    public void testGetDateCmdLineArgs() {
        Assert.assertEquals(Paru.getDateCmdLineArgs("file.zip", 1, null, null, false), new String[] { "file.zip", "y1",
                "", "" });
        Assert.assertEquals(Paru.getDateCmdLineArgs("file.zip", 1, null, "b", false), new String[] { "file.zip", "y1",
                ",", "b" });
        Assert.assertEquals(Paru.getDateCmdLineArgs("file.zip", 1, "a,b", "c,d", false), new String[] { "file.zip",
                "y1", "a,b", "c,d" });
        Assert.assertEquals(Paru.getDateCmdLineArgs("file.zip", 1, "a,b", "c,d", true), new String[] { "\"file.zip\"",
                "y1", "a,b", "c,d" });
        Assert.assertEquals(Paru.getDateCmdLineArgs("", 1, "a,b", "c,d", false),
                new String[] { "", "y1", "a,b", "c,d" });
        Assert.assertEquals(Paru.getDateCmdLineArgs(null, 1, "a,b", "c,d", false), new String[] { "", "y1", "a,b",
                "c,d" });
        Assert.assertEquals(Paru.getDateCmdLineArgs("", 1, "a,b", "c,d", true), new String[] { "\"<Input Filename>\"",
                "y1", "a,b", "c,d" });
    }

    @Test
    public void testGetCharCmdLineArgs() {
        // Missing filename
        Assert.assertEquals(
                Paru.getCharCmdLineArgs(null, true, false, false, false, false, "", 1, 2, "", "", "", "", false),
                new String[] { "", "0-9", "1", "2", "", "", "", "" });
        Assert.assertEquals(
                Paru.getCharCmdLineArgs(null, true, false, false, false, false, "", 1, 2, "", "", "", "", true),
                new String[] { "\"<Input Filename>\"", "0-9", "1", "2", "", "", "", "" });

        // Simple case
        Assert.assertEquals(
                Paru.getCharCmdLineArgs("file.zip", true, false, false, false, false, "", 1, 2, "", "", "", "", false),
                new String[] { "file.zip", "0-9", "1", "2", "", "", "", "" });
        Assert.assertEquals(
                Paru.getCharCmdLineArgs("file.zip", true, false, false, false, false, "", 1, 2, "", "", "", "", true),
                new String[] { "\"file.zip\"", "0-9", "1", "2", "", "", "", "" });

        // All null
        Assert.assertEquals(Paru.getCharCmdLineArgs("file.zip", true, false, false, false, false, null, 1, 2, null,
                null, null, null, false), new String[] { "file.zip", "0-9", "1", "2", "", "", "", "" });
        Assert.assertEquals(Paru.getCharCmdLineArgs("file.zip", true, false, false, false, false, null, 1, 2, null,
                null, null, null, true), new String[] { "\"file.zip\"", "0-9", "1", "2", "", "", "", "" });

        // All filled
        Assert.assertEquals(Paru.getCharCmdLineArgs("file.zip", true, true, true, true, true, null, 1, 2, null, "zz",
                "a,b", "c,d", false), new String[] { "file.zip", "0-9a-zA-Z!:w", "1", "2", "0", "zz", "a,b", "c,d" });
        Assert.assertEquals(Paru.getCharCmdLineArgs("file.zip", true, true, true, true, true, null, 1, 2, null, "zz",
                "a,b", "c,d", true), new String[] { "\"file.zip\"", "0-9a-zA-Z!:w", "1", "2", "0", "zz", "a,b", "c,d" });

        // Combinations of pattern
        Assert.assertEquals(
                Paru.getCharCmdLineArgs("file.zip", false, false, true, false, true, "", 1, 2, "", "", "", "", false),
                new String[] { "file.zip", "A-Z:w", "1", "2", "", "", "", "" });
        Assert.assertEquals(
                Paru.getCharCmdLineArgs("file.zip", false, true, false, true, false, "", 1, 2, "", "", "", "", false),
                new String[] { "file.zip", "a-z!", "1", "2", "", "", "", "" });

        // No start/end but have prefix/suffix
        Assert.assertEquals(
                Paru.getCharCmdLineArgs("file.zip", true, false, false, false, false, "", 1, 2, "", "", "", "x", true),
                new String[] { "\"file.zip\"", "0-9", "1", "2", "_", "_", ",", "x" });
        Assert.assertEquals(
                Paru.getCharCmdLineArgs("file.zip", true, false, false, false, false, "", 1, 2, "", "", "x", "", true),
                new String[] { "\"file.zip\"", "0-9", "1", "2", "_", "_", "x", "" });
        Assert.assertEquals(
                Paru.getCharCmdLineArgs("file.zip", true, false, false, false, false, "", 1, 2, "", "", "x", "y", true),
                new String[] { "\"file.zip\"", "0-9", "1", "2", "_", "_", "x", "y" });

        // Have start / no end
        Assert.assertEquals(
                Paru.getCharCmdLineArgs("file.zip", true, false, false, false, false, "", 1, 2, "0", "", "", "", true),
                new String[] { "\"file.zip\"", "0-9", "1", "2", "0", "", "", "" });
        Assert.assertEquals(
                Paru.getCharCmdLineArgs("file.zip", true, false, false, false, false, "", 1, 2, "0", "", "x", "", true),
                new String[] { "\"file.zip\"", "0-9", "1", "2", "0", "", "x", "" });
        Assert.assertEquals(
                Paru.getCharCmdLineArgs("file.zip", true, false, false, false, false, "", 1, 2, "0", "", "", "x", true),
                new String[] { "\"file.zip\"", "0-9", "1", "2", "0", "", ",", "x" });
        Assert.assertEquals(Paru.getCharCmdLineArgs("file.zip", true, false, false, false, false, "", 1, 2, "0", "",
                "x", "y", true), new String[] { "\"file.zip\"", "0-9", "1", "2", "0", "", "x", "y" });

        // Have end / no start
        Assert.assertEquals(
                Paru.getCharCmdLineArgs("file.zip", true, false, false, false, false, "", 1, 2, "", "99", "", "", true),
                new String[] { "\"file.zip\"", "0-9", "1", "2", "0", "99", "", "" });
        Assert.assertEquals(Paru.getCharCmdLineArgs("file.zip", true, false, false, false, false, "", 1, 2, "", "99",
                "x", "", true), new String[] { "\"file.zip\"", "0-9", "1", "2", "0", "99", "x", "" });
        Assert.assertEquals(Paru.getCharCmdLineArgs("file.zip", true, false, false, false, false, "", 1, 2, "", "99",
                "", "x", true), new String[] { "\"file.zip\"", "0-9", "1", "2", "0", "99", ",", "x" });
        Assert.assertEquals(Paru.getCharCmdLineArgs("file.zip", true, false, false, false, false, "", 1, 2, "", "99",
                "x", "y", true), new String[] { "\"file.zip\"", "0-9", "1", "2", "0", "99", "x", "y" });

        // TODO:Invalid pattern (not support custom at this moment)
        Assert.assertEquals(Paru.getCharCmdLineArgs("file.zip", true, false, false, false, false, "~", 1, 2, null,
                null, null, null, false), new String[] { "file.zip", "0-9", "1", "2", "", "", "", "" });
    }

    @Test
    public void testDatePatternY() throws Exception {
        Paru.main(new String[] { "src/test/resources/2311.zip", "y1" });
    }

    @Test
    public void testDatePattern() throws Exception {
        Paru.main(new String[] { "src/test/resources/2311.zip", "y1", "a,b", "c,d" });
    }
}
