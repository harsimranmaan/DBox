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
    private File clientLog;
    private HashMap<String, String> clientMap = new HashMap<>();
    private HashMap<String, String> serverMap = new HashMap<>();
    private HashMap<String, String> tempMap = new HashMap<>();
    private HashMap<String, String> conflict = new HashMap<>();

    public DirectoryManager(String localDirPath) throws IOException, ClassNotFoundException
    {
        initialize();
    }

    public void initialize() throws IOException, ClassNotFoundException
    {
        this.clientLog = new File("dblike.data");
        if (!clientLog.exists())
        {
            writeClientLog();
        }
        else
        {
            mergeHashes();
            tempMap.putAll(clientMap);
            readClientLog();
        }
    }

    public void writeClientLog() throws IOException
    {
        FileOutputStream fos = new FileOutputStream(clientLog);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        //anotherMap.putAll(map);
        oos.writeObject(clientMap);
        oos.flush();
        oos.close();
    }

    public void readClientLog() throws IOException, ClassNotFoundException
    {
        FileInputStream fis = new FileInputStream(clientLog);
        ObjectInputStream ois = new ObjectInputStream(fis);
        tempMap = (HashMap<String, String>) ois.readObject();
        ois.close();
    }

    public void mergeHashes()
    {
        if (!tempMap.isEmpty())
        {
            for (String filename : clientMap.keySet())
            {
                String compareHash = tempMap.get(filename);
                if (compareHash != clientMap.get(filename))
                {
                    conflict.put(filename, null);
                    //                    clientMap.put(filename, clientMap.get(filename));
                    //                    tempMap.remove(filename);

                }
            }
        }
    }
}
