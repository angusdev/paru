package org.ellab.paru.filehandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.exception.ZipExceptionConstants;
import net.lingala.zip4j.model.FileHeader;

public class Zip4jFileHandler implements FileHandler {
    private ZipFile zipfile;
    private List<FileHeader> fileHeaders;

    @SuppressWarnings("unchecked")
    public Zip4jFileHandler(String filename) throws FileNotFoundException, FileNotEncryptedException,
            InvalidFileFormatException {
        try {
            zipfile = new ZipFile(new File(filename));
            if (!zipfile.isEncrypted()) {
                throw new FileNotEncryptedException(filename);
            }
            fileHeaders = zipfile.getFileHeaders();
        }
        catch (ZipException ex) {
            zipfile = null;
            throw new InvalidFileFormatException(filename, ex);
        }
    }

    @Override
    public boolean verify(String password) {
        InputStream is = null;
        try {
            zipfile.setPassword(password);

            for (FileHeader fileHeader : fileHeaders) {
                try {
                    is = zipfile.getInputStream(fileHeader);
                    byte[] b = new byte[4096];
                    while (is.read(b) != -1) {
                        // Do nothing as we just want to verify password
                    }
                    is.close();
                }
                catch (ZipException ex) {
                    if (ex.getCode() == ZipExceptionConstants.WRONG_PASSWORD) {
                        // System.out.println("Wrong password");
                        return false;
                    }
                    else {
                        // Corrupt file
                        return false;
                    }
                }
                catch (IOException ex) {
                    // System.out.println("Most probably wrong password.");
                    return false;
                }
            }

            return true;
        }
        catch (Exception ex) {
            System.out.println("Some other exception occurred");
            ex.printStackTrace();
            return false;
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException ex) {
                    ;
                }
            }
        }
    }

    @Override
    public void close() {
        zipfile = null;
    }

}