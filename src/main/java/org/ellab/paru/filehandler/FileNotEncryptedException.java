package org.ellab.paru.filehandler;

public class FileNotEncryptedException extends Exception {
    private static final long serialVersionUID = -6634916200219704178L;

    public FileNotEncryptedException() {
        super();
    }

    public FileNotEncryptedException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileNotEncryptedException(String message) {
        super(message);
    }

    public FileNotEncryptedException(Throwable cause) {
        super(cause);
    }

}
