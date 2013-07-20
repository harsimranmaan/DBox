/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Client;

import dBox.ClientAction;
import dBox.FileDeleter;
import dBox.FilePacket;
import dBox.FileSender;
import dBox.HashManager;
import dBox.IFileServer;
import dBox.IServerDetailsGetter;
import dBox.ServerDetails;
import dBox.utils.ConfigManager;
import dBox.utils.CustomLogger;
import dBox.utils.Hashing;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
    private ConfigManager config;
    private HashMap<Path, String> fileCurrentHash;
    private HashMap<Path, String> fileEvent;
    private WatchDir dirWatcher;
    private boolean keepProcessing;
    private Path folder;
    private IFileServer receiver;
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
        this.hashManager = new HashManager(hashFile, fileEvent);
        this.config = config;
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
                    ClientAction action;
                    if (path.toString().contains("~"))
                    {
                        fileEvent.remove(path);
                    }
                    else
                    {
                        switch (fileEvent.get(path))
                        {
                            case "ENTRY_DELETE":
                                String oldHash = hashManager.getValue(path);
                                action = receiver.actionOnDelete(getServerPathFull(path), oldHash);

                                switch (action)
                                {
                                    case DOWNLOAD:
                                        hashManager.updateHash(path, downLoadFile(path));
                                        CustomLogger.log("Downloaded " + path);
                                        break;

                                    case NOTHING:
                                    default:
                                        hashManager.deleteHash(path);
                                        break;
                                }
                                fileEvent.remove(path);
                                break;
                            case "ENTRY_CREATE":
                            case "ENTRY_MODIFY":
                                if (Files.exists(path) && Files.size(path) > 0)
                                {
                                    if (!Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS))
                                    {
                                        String fileHash = Hashing.getSHAChecksum(path.toString());
                                        action = receiver.actionOnModify(getServerPathFull(path), fileHash, hashManager.getValue(path));
                                        System.out.println(action);
                                        try
                                        {
                                            switch (action)
                                            {
                                                case UPLOAD:
                                                    upload(path);
                                                    hashManager.updateHash(path, fileHash);
                                                    CustomLogger.log("Uploaded " + path);
                                                    break;
                                                case DOWNLOAD:

                                                    hashManager.updateHash(path, downLoadFile(path));

                                                    CustomLogger.log("Downloaded " + path);
                                                    break;
                                                case CONFLICT:
                                                    resolveConflict(path);
                                                    //Handle hash change
                                                    CustomLogger.log("Conflict Detected " + path);
                                                    break;
                                                case DELETE:
                                                    removeFile(path);
                                                    CustomLogger.log("Deleted " + path);
                                                    hashManager.deleteHash(path);
                                                    break;
                                                case NOTHING:
                                                default:
                                                    break;
                                            }
                                        }
                                        catch (FileNotFoundException ex)
                                        {
                                            //File removed while in processing
                                            fileEvent.put(path, "ENTRY_DELETE");
                                        }
                                    }
                                    else
                                    {
                                        //just make it traceable
                                        hashManager.updateHash(path, "0");
                                    }
                                    fileEvent.remove(path);
                                }
                                else
                                {
                                    fileEvent.put(path, "ENTRY_DELETE");
                                }
                                break;


                            default:
                                CustomLogger.log(fileEvent.get(path));
                                break;
                        }
                    }
                }

                Thread.sleep(20000);
            }
            catch (Exception ex)
            {
                CustomLogger.log(ex.getCause().getMessage());
            }
        }
    }

    /**
     * Downloads the file and returns its hash
     * <p/>
     * @param path
     */
    private String downLoadFile(Path path) throws RemoteException
    {
        FilePacket download = receiver.download(getServerPathFull(path));
        try
        {
            OutputStream out = new FileOutputStream(path.toString());
            download.copy(out);
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(DirectoryManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Hashing.getSHAChecksum(path.toString());
    }

    /**
     * removes the path file
     * <p/>
     * @param path <p/>
     * @throws IOException
     */
    private void removeFile(Path path) throws IOException
    {
        FileDeleter.delete(path, this.folder);
    }

    private void upload(Path path)
    {
        new FileSender(receiver, getServerPath(path), path).sendFile();
    }

    private void resolveConflict(Path path)
    {
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
     * gets the server path
     * <p/>
     * @param child <p/>
     * @return server path string
     */
    private String getServerPath(Path child)
    {
        // String serverpath = getServerPathFull(child).replaceAll(child.getFileName() + "$", "");
        String serverpath = child.toString().replace(folder + File.separator, "").replaceAll(child.getFileName() + "$", "");
        try
        {
            serverpath = serverpath.replace(File.separator, receiver.pathSeperator());


        }
        catch (RemoteException ex)
        {
            Logger.getLogger(DirectoryManager.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        CustomLogger.log("Serverpath - /" + serverpath);
        return serverpath;
    }

    /**
     *
     * @param child <p/>
     * @return
     */
    private String getServerPathFull(Path child)
    {
        String serverpath = child.toString().replace(folder + File.separator, "");
        try
        {
            serverpath = serverpath.replace(File.separator, receiver.pathSeperator());
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(DirectoryManager.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        CustomLogger.log("Full server path " + serverpath);
        return serverpath;

    }

    /**
     * receives the information about the server
     * <p/>
     * @return the IFileServer object
     * <p/>
     * @throws Exception
     */
    private IFileServer getReceiver() throws Exception
    {
        ServerDetails serverDetails = serverDetailsGetter.getServerDetails(Integer.parseInt(config.getPropertyValue("clusterId")));
        Registry registry = LocateRegistry.getRegistry(serverDetails.getServerName(), serverDetails.getPort());
        receiver = (IFileServer) registry.lookup(IFileServer.class
                .getSimpleName());
        receiver.setDirectory(userHash);
        return receiver;
    }
}
