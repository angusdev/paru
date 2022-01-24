package org.ellab.paru.filehandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileHandlerFactory {
    public static FileHandler getFileHandler(String filename) throws FileNotFoundException, FileNotEncryptedException,
            InvalidFileFormatException, IOException {

        File file = new File(filename);

        if (!file.exists()) {
            throw new FileNotFoundException(filename);
        }

        if (filename.toLowerCase().endsWith(".zip")) {
            return new Zip4jFileHandler(filename);
        }
        else if (filename.toLowerCase().endsWith(".xls") || filename.toLowerCase().endsWith(".xlsx")) {
            return new PoiXlsFileHandler(filename);
        }
        else if (filename.toLowerCase().endsWith(".pdf")) {
            return new ItextFileHandler(filename);
        }

        throw new InvalidFileFormatException(filename);
    }

}
