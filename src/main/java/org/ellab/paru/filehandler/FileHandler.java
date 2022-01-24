package org.ellab.paru.filehandler;

public interface FileHandler {
    public boolean verify(String password);
    
    public void close();
}
