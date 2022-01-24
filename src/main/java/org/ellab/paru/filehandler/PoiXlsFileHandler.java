package org.ellab.paru.filehandler;

import java.io.File;
import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class PoiXlsFileHandler implements FileHandler {
    private String filename;

    public PoiXlsFileHandler(String filename) throws IOException, FileNotEncryptedException, InvalidFileFormatException {
        Workbook wb;
        try {
            wb = WorkbookFactory.create(new File(filename), "", true);
            wb.close();
            throw new FileNotEncryptedException(filename);
        }
        catch (EncryptedDocumentException ex) {
            // normal case, file is encrypted
            this.filename = filename;
        }
        catch (InvalidFormatException ex) {
            throw new InvalidFileFormatException(filename);
        }
        catch (NotOLE2FileException ex) {
            throw new InvalidFileFormatException(filename);
        }
    }

    @Override
    public boolean verify(String password) {
        Workbook wb;
        try {
            wb = WorkbookFactory.create(new File(filename), password, true);
            wb.close();
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