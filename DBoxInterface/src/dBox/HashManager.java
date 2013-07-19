/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox;

import dBox.utils.CustomLogger;
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
    private HashMap<Path, String> fileEvent;

    /**
     *
     * @param filePath
     * @param fileEvent
     */
    public HashManager(Path filePath, HashMap<Path, String> fileEvent)
    {
        this.hashFile = new File(filePath.toString());
        this.fileEvent = fileEvent;
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
     * Wrties the hash map data to the hash file
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
     * Updates the hash of the file and writes it to the hash file
     * <p/>
     * @param key
     * @param hash
     */
    public void updateHash(String key, String hash)
    {
        lastKnownServerHashes.put(key, hash);
        writeHashes();
    }
}
