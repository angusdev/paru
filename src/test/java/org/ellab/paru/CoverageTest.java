package org.ellab.paru;

import org.ellab.paru.filehandler.FileHandlerFactory;
import org.ellab.paru.filehandler.FileNotEncryptedException;
import org.ellab.paru.filehandler.InvalidFileFormatException;
import org.ellab.paru.pattern.CharPattern;
import org.testng.annotations.Test;

public class CoverageTest {
    @Test(groups = { "coverage" })
    public void testCoverage() {
        CharPattern.main(null);
        Performance.main(null);
        FormatDuration.main(null);
        new FileHandlerFactory();
    }

    @Test(groups = { "coverage" })
    public void testFileNotEncryptedException() {
        new FileNotEncryptedException();
        new FileNotEncryptedException(new Exception());
        new FileNotEncryptedException("", new Exception());
    }

    @Test(groups = { "coverage" })
    public void testInvalidFileFormatException() {
        new InvalidFileFormatException();
        new InvalidFileFormatException(new Exception());
    }
}
