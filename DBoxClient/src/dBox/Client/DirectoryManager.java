/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Client;

import dBox.FileSender;
import dBox.HashManager;
import dBox.IFileReceiver;
import dBox.IServerDetailsGetter;
import dBox.ServerDetails;
import dBox.utils.ConfigManager;
import dBox.utils.CustomLogger;
import dBox.utils.Hashing;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
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
//    private ConfigManager config;
    private HashMap<Path, String> fileCurrentHash;
    private HashMap<Path, String> fileEvent;
    private WatchDir dirWatcher;
    private boolean keepProcessing;
    private Path folder;
    private IFileReceiver receiver;
    private String userHash;
    private ArrayList<Path> ignorePath;
    private HashManager hashManager;

    /**
     * instantiates the class property
     * <p/>
     * @param hash
     * @param serverDetailsGetter
     * @param config              <p/>
     * @throws IOException
     */
    public DirectoryManager(String hash, IServerDetailsGetter serverDetailsGetter, ConfigManager config) throws IOException
    {
        this.serverDetailsGetter = serverDetailsGetter;
        this.userHash = hash;
        fileCurrentHash = new HashMap<>();
        fileEvent = new HashMap<>();
        ignorePath = new ArrayList<>();
        this.folder = Paths.get(config.getPropertyValue("folder"));
        Path hashFile = Paths.get(this.folder.toString() + File.separator + config.getPropertyValue("hashFile"));
        ignorePath.add(hashFile);

        this.dirWatcher = new WatchDir(folder, true, fileCurrentHash, fileEvent, ignorePath);
        this.hashManager = new HashManager(hashFile);

        keepProcessing = true;
    }

    /**
     * start monitoring the path directory
     */
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
                                String fileHash = Hashing.getSHAChecksum(path.toString());
                                if (!hashManager.hashMatches(path, fileHash))
                                {
                                    new FileSender(receiver, getServerPath(path), path).sendFile();
                                    CustomLogger.log("Uploaded " + path);
                                    hashManager.updateHash(path.toString(), fileHash);
                                }
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
                // CustomLogger.log(ex.getMessage());
            }
        }
    }

    /**
     * stops monitoring the directory
     */
    public void stopMonitor()
    {

        dirWatcher.interrupt();
        keepProcessing = false;
    }

    @Override
    public void run()
    {
        this.dirWatcher.start();
        startMonitor();
    }

    /**
     *
     * @param child <p/>
     * @return
     */
    private String getServerPath(Path child)
    {
        String serverpath = child.toString().replace(folder + File.separator, "").replaceAll(child.getFileName() + "$", "");
        try
        {
            serverpath = serverpath.replace(File.separator, receiver.pathSeperator());
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(DirectoryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        CustomLogger.log("Serverpath - /" + serverpath);
        return serverpath;
    }

    /**
     * receives the information about the server
     * <p/>
     * @return the IFilereciver object
     * <p/>
     * @throws Exception
     */
    private IFileReceiver getReceiver() throws Exception
    {
        ServerDetails serverDetails = serverDetailsGetter.getServerDetails();
        Registry registry = LocateRegistry.getRegistry(serverDetails.getServerName(), serverDetails.getPort());
        receiver = (IFileReceiver) registry.lookup(IFileReceiver.class.getSimpleName());
        receiver.setDirectory(userHash);
        return receiver;
    }
}
