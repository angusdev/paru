package org.ellab.paru.filehandler;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.itextpdf.text.exceptions.BadPasswordException;
import com.itextpdf.text.pdf.PdfReader;

public class ItextFileHandler implements FileHandler {
    private String filename;

    public ItextFileHandler(String filename) throws FileNotFoundException, FileNotEncryptedException,
            InvalidFileFormatException {

        try {
            PdfReader reader = new PdfReader(filename, "".getBytes());
            reader.close();
            throw new FileNotEncryptedException(filename);
        }
        catch (BadPasswordException ex) {
            this.filename = filename;
        }
        catch (IOException ex) {
            throw new InvalidFileFormatException(filename, ex);
        }
    }

    @Override
    public boolean verify(String password) {
        try {
            PdfReader reader = new PdfReader(filename, password.getBytes());
            reader.close();
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    @Override
    public void close() {
    }

}