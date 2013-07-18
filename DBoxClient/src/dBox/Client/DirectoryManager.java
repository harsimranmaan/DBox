/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Client;

import dBox.FileDetail;
import dBox.FileSender;
import dBox.IFileReceiver;
import dBox.IServerDetailsGetter;
import dBox.ServerDetails;
import dBox.utils.ConfigManager;
import dBox.utils.CustomLogger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kuntal
 */
public class DirectoryManager extends Thread
{

    private IServerDetailsGetter serverDetailsGetter;
    private FileDetail file;
    private File logFile;
    private HashMap<String, String> writeMap = new HashMap<>();
    private HashMap<String, String> readMap = new HashMap<>();
    private HashMap<String, String> serverMap = new HashMap<>();
    private HashMap<String, String> tempMap = new HashMap<>();
    private HashMap<String, String> conflict = new HashMap<>();
    private ConfigManager config;
    private HashMap<String, String> fileHash;
    private HashMap<Path, String> fileEvent;
    private WatchDir dirWatcher;
    private boolean keepProcessing;
    private Path folder;
    private IFileReceiver receiver;

    public DirectoryManager(IServerDetailsGetter serverDetailsGetter, ConfigManager config) throws IOException
    {
        this.serverDetailsGetter = serverDetailsGetter;
        fileHash = new HashMap<>();
        fileEvent = new HashMap<>();
        this.folder = Paths.get(config.getPropertyValue("folder"));
        this.dirWatcher = new WatchDir(folder, true, fileHash, fileEvent);
        this.config = config;

        keepProcessing = true;
    }

    private void startMonitor()
    {
        while (keepProcessing)
        {
            try
            {
                for (Path path : fileEvent.keySet())
                {
                    receiver = getReceiver();
                    switch (fileEvent.get(path))
                    {
                        case "ENTRY_CREATE":

                            if (!Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS))
                            {
                                new FileSender(receiver, getServerPath(path), path).sendFile();
                                fileEvent.remove(path);
                            }
                            break;
                        case "ENTRY_DELETE":
                            break;
                        case "ENTRY_MODIFY":
                            if (!Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS))
                            {
                                new FileSender(receiver, getServerPath(path), path).sendFile();
                                fileEvent.remove(path);
                            }
                            break;
                        default:
                            CustomLogger.log("Corrupt event");
                            break;
                    }
                }
                Thread.sleep(10000);
            }
            catch (Exception ex)
            {
            }
        }
    }

    public void stopMonitor()
    {

        dirWatcher.interrupt();
        keepProcessing = false;
    }

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

    @Override
    public void run()
    {
        this.dirWatcher.start();
        startMonitor();
    }

    private String getServerPath(Path child)
    {
        String serverpath = child.toString().replace(folder + File.separator, "");
        try
        {
            serverpath = serverpath.replace(File.separator, receiver.pathSeperator());
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(DirectoryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        CustomLogger.log("Serverpath " + serverpath);
        return serverpath;
    }

    private IFileReceiver getReceiver() throws Exception
    {
        ServerDetails serverDetails = serverDetailsGetter.getServerDetails();
        Registry registry = LocateRegistry.getRegistry(serverDetails.getServerName(), serverDetails.getPort());
        receiver = (IFileReceiver) registry.lookup(IFileReceiver.class.getSimpleName());
        receiver.setDirectory(config.getPropertyValue("hash"));
        return receiver;
    }
}
