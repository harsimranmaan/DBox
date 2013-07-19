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
 *
 * @author harsimran.maan
 */
public class HashManager
{

    private File hashFile;
    private HashMap<String, String> lastKnownServerHashes;
    private HashMap<Path, String> fileEvent;

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

    public boolean hashMatches(Path key, String clientHash)
    {
        return clientHash.equals(lastKnownServerHashes.get(key.toString()));
    }

    public void updateHash(String key, String hash)
    {
        lastKnownServerHashes.put(key, hash);
        writeHashes();
    }
}
