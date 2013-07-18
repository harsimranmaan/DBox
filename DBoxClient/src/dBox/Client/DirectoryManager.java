/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Client;

import dBox.FileDetail;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author Kuntal
 */
public class DirectoryManager implements Serializable
{

    private FileDetail file;
    private File logFile;
    private HashMap<String, String> writeMap = new HashMap<>();
    private HashMap<String, String> readMap = new HashMap<>();
    private HashMap<String, String> serverMap = new HashMap<>();
    private HashMap<String, String> tempMap = new HashMap<>();
    private HashMap<String, String> conflict = new HashMap<>();

    public DirectoryManager(String logFile) throws IOException, ClassNotFoundException
    {
        this.logFile = new File(logFile);
        this.writeMap = null;

    }

//            mergeHashes();
//            tempMap.putAll(clientMap);
    public boolean writeLog(File logFile, HashMap<String, String> map) throws IOException
    {
        writeMap.putAll(map);
        FileOutputStream fos = new FileOutputStream(logFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        //anotherMap.putAll(map);
        oos.writeObject(writeMap);
        oos.flush();
        oos.close();
        return true;
    }

    public HashMap<String, String> readLog(File logFile) throws IOException, ClassNotFoundException
    {
        FileInputStream fis = new FileInputStream(logFile);
        ObjectInputStream ois = new ObjectInputStream(fis);
        readMap = (HashMap<String, String>) ois.readObject();
        ois.close();
        return readMap;
    }

    public void mergeHashes(HashMap<String, String> map, HashMap<String, String> newMap)
    {
        for (String filename : map.keySet())
        {
            String compareHash = newMap.get(filename);
            if (!compareHash.equals(map.get(filename)))
            {
                conflict.put(filename, null);
                //                    clientMap.put(filename, clientMap.get(filename));
                //                    tempMap.remove(filename);

            }
        }
    }
}
