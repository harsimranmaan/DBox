/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class represents the operation regarding file and its hash
 * <p>
 * Write hash file
 * <p/>
 * <p>
 * read from hash file the hashmap
 * <p/>
 * <p>
 * update the existed hashmap
 * <p/>
 * @author harsimran.maan
 */
public class HashManager
{

    private File hashFile;
    private HashMap<String, String> lastKnownServerHashes;
    //   private HashMap<Path, String> fileEvent;
    private String hashFilePath;

    /**
     * Instantiates the Hash Manager attributes
     * <p/>
     * @param filePath
     * @param fileEvent
     */
    public HashManager(Path filePath)
    {

        this.hashFilePath = filePath.toString();
        this.hashFile = new File(hashFilePath);

        lastKnownServerHashes = new HashMap<>();
        if (hashFile.exists())
        {
            readHashes();
        }
    }

    /**
     * Reads the data from the hash file
     */
    private void readHashes()
    {
        try
        {
            FileInputStream fis = new FileInputStream(hashFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            lastKnownServerHashes = (HashMap<String, String>) ois.readObject();
            ois.close();
            fis.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(HashManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (ClassNotFoundException ex)
        {
            Logger.getLogger(HashManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Writes the hash map data to the hash file
     */
    private void writeHashes()
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(hashFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(lastKnownServerHashes);
            oos.flush();
            oos.close();
            fos.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(HashManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * checks the hash map of two files
     * <p/>
     * @param key
     * @param clientHash <p/>
     * @return true of matches otherwise false
     */
    public boolean hashMatches(Path key, String clientHash)
    {
        return clientHash.equals(lastKnownServerHashes.get(key.toString()));
    }

    /**
     * Get the hash value for a path
     * <p/>
     * @param key <p/>
     * @return
     */
    public String getValue(Path key)
    {
        return (lastKnownServerHashes.get(key.toString()) != null) ? lastKnownServerHashes.get(key.toString()) : "";
    }

    /**
     * Updates the hash of the file and writes it to the hash file
     * <p/>
     * @param key
     * @param hash
     */
    public void updateHash(Path key, String hash)
    {
        lastKnownServerHashes.put(key.toString(), hash);
        writeHashes();
    }

    /**
     * The function to delete the entry from the hash-map
     * <p/>
     * @param key
     */
    public void deleteHash(Path key)
    {
        lastKnownServerHashes.remove(key.toString());
        writeHashes();
    }

    /**
     * @return the hashFilePath
     */
    public String getHashFilePath()
    {
        return hashFilePath;
    }

    /**
     * The function to get the detail of the deleted file
     * <p/>
     * @param currentFileHashes
     * <p/>
     * @return the hash-map of the deleted file
     */
    public HashMap<String, FileDetail> getDeletedFiles(HashMap<String, FileDetail> currentFileHashes)
    {
        HashMap<String, FileDetail> deleted = new HashMap<>();

        for (String key : lastKnownServerHashes.keySet())
        {
            if (!currentFileHashes.containsKey(key))
            {
                deleted.put(key, new FileDetail(lastKnownServerHashes.get(key), ""));
            }
        }
        return deleted;
    }
}
