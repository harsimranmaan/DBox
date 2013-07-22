/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox;

import java.io.Serializable;

/**
 * Interface for the File details
 * <p/>
 * @author Kuntal
 */
public class FileDetail implements Serializable
{

    private String oldHash;
    private String fileHash;

    /**
     * setting up the old and new hash of the file
     * <p/>
     * @param oldHash
     * @param newHash
     */
    public FileDetail(String oldHash, String newHash)
    {
        this.oldHash = oldHash;
        this.fileHash = newHash;

    }

    /**
     * @return the synchFile
     */
    public String getOldHash()
    {
        return oldHash;

    }

    /**
     * @return the fileHash
     */
    public String getNewHash()
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
