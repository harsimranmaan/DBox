/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox;

import java.io.File;

/**
 *
 * @author Kuntal
 */
public class FileDetail
{

    private File synchFile;
    private String fileHash;

    public FileDetail(File file, String hash)
    {
        this.synchFile = file;
        this.fileHash = hash;

    }

    /**
     * @return the synchFile
     */
    public File getSynchFile()
    {
        return synchFile;
    }

    /**
     * @param synchFile the synchFile to set
     */
    public void setSynchFile(File synchFile)
    {
        this.synchFile = synchFile;
    }

    /**
     * @return the fileHash
     */
    public String getFileHash()
    {
        return fileHash;
    }

    /**
     * @param fileHash the fileHash to set
     */
    public void setFileHash(String fileHash)
    {
        this.fileHash = fileHash;
    }
}
