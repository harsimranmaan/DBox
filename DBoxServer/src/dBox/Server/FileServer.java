/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Server;

import dBox.ClientAction;
import dBox.FileDeleter;
import dBox.FileDetail;
import dBox.FilePacket;
import dBox.FileReceiver;
import dBox.IFileServer;
import dBox.ServerDetails;
import dBox.utils.ConfigManager;
import dBox.utils.CustomLogger;
import dBox.utils.Hashing;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author harsimran.maan
 */
public class FileServer extends UnicastRemoteObject implements IFileServer, Serializable
{

    private static final long serialVersionUID = 22122L;
    private String directory;
    private FileReceiver receiver;
    private String clientBase;
    private String clientSeperator;
    private PeerDetailsGetter peerDetailsGetter;
    private String userHash;

    public FileServer(PeerDetailsGetter peerDetailsGetter) throws RemoteException
    {
        super();
        this.peerDetailsGetter = peerDetailsGetter;
    }

    @Override
    public void setDirectory(String directory, String clientBase, String clientSeperator) throws RemoteException
    {
        CustomLogger.log("FileServer > setDirectory : directory " + directory);
        this.userHash = directory;
        this.directory = System.getProperty("user.home") + File.separator + ConfigManager.getInstance().getPropertyValue("rootPath") + File.separator + directory;
        this.receiver = new FileReceiver(this.directory);
        this.clientBase = clientBase;
        this.clientSeperator = clientSeperator;
    }

    @Override
    public void receiveFile(String path, FilePacket packet) throws RemoteException
    {
        try
        {
            ServerDetails monitor = getMonitor();
            Registry registry = LocateRegistry.getRegistry(monitor.getServerName(), monitor.getPort());
            IFileServer monitorReceiver = (IFileServer) registry.lookup(IFileServer.class
                    .getSimpleName());
            monitorReceiver.setDirectory(userHash, clientBase, clientSeperator);
            monitorReceiver.receiveFile(path, packet);
        }
        catch (Exception ex)
        {
            CustomLogger.log(ex.getMessage());
        }
        receiver.receiveFile(path, packet);

    }

    @Override
    public String pathSeperator() throws RemoteException
    {
        return File.separator;
    }

    @Override
    public FilePacket download(String path) throws RemoteException
    {
        CustomLogger.log("FileServer > FilePacket : path " + path);
        Path myPath = Paths.get(this.directory + File.separator + path);
        System.out.println(myPath.toString());
        return new FilePacket(myPath);
    }

    private String getServerPath(String path)
    {
        path = path.replace(clientBase, directory);
        path = path.replace(clientSeperator, File.separator);
        CustomLogger.log("Processing on server path " + path);
        return path;
    }

    private String getClientPath(Path serverPath)
    {
        String clientPth = serverPath.toString().replace(directory, clientBase);
        clientPth = clientPth.replace(File.separator, clientSeperator);
        CustomLogger.log("New client path " + clientPth);
        return clientPth;
    }

    @Override
    public HashMap<String, ClientAction> getClientActions(HashMap<String, FileDetail> currentFiles, HashMap<String, FileDetail> deletedFiles) throws RemoteException
    {
        HashMap<String, ClientAction> clientActions = new HashMap<>();
        for (String key : deletedFiles.keySet())
        {
            String serverPath = getServerPath(key);
            File theFile = new File(serverPath);
            if (theFile.exists())
            {
                String serverHash = Hashing.getSHAChecksum(serverPath);
                if (deletedFiles.get(key).getOldHash().equals(serverHash))
                {
                    CustomLogger.log("Deleting " + serverPath);
                    try
                    {
                        ServerDetails monitor = getMonitor();
                        Registry registry = LocateRegistry.getRegistry(monitor.getServerName(), monitor.getPort());
                        IFileServer monitorReceiver = (IFileServer) registry.lookup(IFileServer.class
                                .getSimpleName());
                        monitorReceiver.setDirectory(userHash, clientBase, clientSeperator);
                        monitorReceiver.delete(serverPath, directory);
                    }
                    catch (Exception ex)
                    {
                        CustomLogger.log(ex.getMessage());
                    }
                    delete(serverPath, directory);
                }
                else
                {
                    clientActions.put(key, ClientAction.DOWNLOAD);
                }
            }
        }
        for (String key : currentFiles.keySet())
        {
            String serverPath = getServerPath(key);
            File theFile = new File(serverPath);
            FileDetail detail = currentFiles.get(key);
            if (!theFile.exists())
            {

                if (detail.getNewHash().equals(detail.getOldHash()))
                {
                    clientActions.put(key, ClientAction.DELETE);
                }
                else
                {
                    clientActions.put(key, ClientAction.UPLOAD);
                }
            }
            else
            {
                String serverHash = Hashing.getSHAChecksum(serverPath);
                CustomLogger.log("On Server hash " + serverHash);
                if (!serverHash.equals(detail.getNewHash()))
                {

                    if (serverHash.equals(detail.getOldHash()))
                    {
                        clientActions.put(key, ClientAction.UPLOAD);
                    }
                    else if (detail.getOldHash().equals(detail.getNewHash()))
                    {
                        clientActions.put(key, ClientAction.DOWNLOAD);
                    }
                    else
                    {
                        clientActions.put(key, ClientAction.CONFLICT);
                    }
                }
            }
        }
        currentFiles.putAll(deletedFiles);
        touchAllFiles(Paths.get(directory), clientActions, currentFiles);
        return clientActions;
    }

    private void touchAllFiles(Path path, HashMap<String, ClientAction> clientActions, HashMap<String, FileDetail> clientList)
    {

        File folder = new File(path.toString());
        File[] filelist = folder.listFiles();
        Path filepath;
        for (int i = 0; i < filelist.length; i++)
        {
            filepath = filelist[i].toPath();
            if (Files.isDirectory(filepath, NOFOLLOW_LINKS))
            {
                touchAllFiles(filepath, clientActions, clientList);
            }
            else
            {
                String clientPath = getClientPath(filepath);
                if (!clientList.containsKey(clientPath))
                {
                    CustomLogger.log("Mark for download client path " + clientPath);
                    clientActions.put(clientPath, ClientAction.DOWNLOAD);
                }
            }
        }
    }

    private ServerDetails getMonitor() throws Exception
    {
        return peerDetailsGetter.getMonitorDetails();
    }

    @Override
    public void delete(String serverPath, String upto) throws RemoteException
    {
        try
        {
            FileDeleter.delete(Paths.get(serverPath), Paths.get(upto));
        }
        catch (IOException ex)
        {
            Logger.getLogger(FileServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
