package org.ellab.paru.filehandler;

public class InvalidFileFormatException extends Exception {
    private static final long serialVersionUID = -7883892890595223581L;

    public InvalidFileFormatException() {
        super();
    }

    public InvalidFileFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidFileFormatException(String message) {
        super(message);
    }

    public InvalidFileFormatException(Throwable cause) {
        super(cause);
    }

}
