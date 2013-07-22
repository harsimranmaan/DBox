/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Client;

import dBox.ClientAction;
import static dBox.ClientAction.CONFLICT;
import static dBox.ClientAction.DOWNLOAD;
import static dBox.ClientAction.UPLOAD;
import dBox.FileDeleter;
import dBox.FileDetail;
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
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private HashMap<String, FileDetail> currentFileHashes;
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
        currentFileHashes = new HashMap<>();
        ignorePath = new ArrayList<>();
        this.folder = Paths.get(config.getPropertyValue("folder"));
        Path hashFile = Paths.get(this.folder.toString() + File.separator + config.getPropertyValue("hashFile"));
        ignorePath.add(hashFile);
        this.hashManager = new HashManager(hashFile);
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
                currentFileHashes = new HashMap<>();
                touchAllFiles(this.folder);
                HashMap<String, FileDetail> deletedFiles = hashManager.getDeletedFiles(currentFileHashes);
                getReceiver();
                HashMap<String, ClientAction> actionMap = receiver.getClientActions(currentFileHashes, deletedFiles);
                for (String key : actionMap.keySet())
                {
                    switch (actionMap.get(key))
                    {
                        case UPLOAD:
                            Path upPath = Paths.get(key);
                            hashManager.updateHash(upPath, upload(upPath));
                            CustomLogger.log("Upload on client " + key);
                            break;
                        case DOWNLOAD:
                            Path downPath = Paths.get(key);
                            hashManager.updateHash(downPath, downLoadFile(downPath));
                            CustomLogger.log("Downloaded on client " + key);
                            break;
                        case CONFLICT:
                            Path conflictPath = Paths.get(key);
                            String newPath = resolveConflict(conflictPath);
                            hashManager.updateHash(Paths.get(newPath), Hashing.encryptSHA(newPath));
                            hashManager.deleteHash(conflictPath);
                            CustomLogger.log("Conflict detected on client " + key);
                            break;
                        case DELETE:
                            Path deletePath = Paths.get(key);
                            removeFile(deletePath);
                            hashManager.deleteHash(deletePath);
                            CustomLogger.log("Deleted on client " + key);
                            break;
                        default:
                            break;
                    }
                }
            }
            catch (Exception ex)
            {
                CustomLogger.log(ex.getMessage());
            }
            finally
            {
                try
                {
                    Thread.sleep(Integer.parseInt(config.getPropertyValue("pollInterval")));
                }
                catch (InterruptedException ex)
                {
                }
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
            File theDirCheck = path.getParent().toFile();
            theDirCheck.mkdirs();
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

    /**
     * The function invoke the upload procedure
     * <p/>
     * @param path file which is to be uploaded
     * <p/>
     * @return hash of the file
     */
    private String upload(Path path)
    {
        new FileSender(receiver, getServerPath(path), path).sendFile();
        return Hashing.getSHAChecksum(path.toString());
    }

    /**
     * The function to resolve the conflict files
     * <p/>
     * @param path conflicted path
     * <p/>
     * @return new path of the conflicted file
     */
    private String resolveConflict(Path path)
    {
        File old = new File(path.toString());
        String newPath = getRenamePath(path);
        File newFile = new File(newPath);
        old.renameTo(newFile);
        old.delete();
        return newPath;
    }

    /**
     * The function to create conflict file path
     * <p/>
     * @param path of the file which has conflicted
     * <p/>
     * @return conflict file path
     */
    private String getRenamePath(Path path)
    {
        return path.toString().replaceAll(path.getFileName() + "$", "~CONFLICT_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + path.getFileName());
    }

    /**
     * stops monitoring the directory
     */
    public void stopMonitor()
    {
        keepProcessing = false;
    }

    @Override
    public void run()
    {
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
     * The function to get the path of the server
     * <p/>
     * @param child the path of the server directory
     * <p/>
     * @return server path in String format
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
        receiver.setDirectory(userHash, folder.toString(), File.separator);
        return receiver;
    }

    /**
     * The function to touch all the files in the user given directory path
     * <p/>
     * @param path give by the user
     */
    private void touchAllFiles(Path path)
    {

        File folder = new File(path.toString());
        File[] filelist = folder.listFiles();
        Path filepath;
        for (int i = 0; i < filelist.length; i++)
        {
            filepath = filelist[i].toPath();
            if (Files.isDirectory(filepath, NOFOLLOW_LINKS))
            {
                touchAllFiles(filepath);
            }
            else
            {

                //ignore certain paths
                if (ignorePath.contains(filepath))
                {
                    continue;
                }
                //Mark any offline changes
                currentFileHashes.put(filepath.toString(), new FileDetail(hashManager.getValue(filepath), Hashing.getSHAChecksum(filepath.toString())));
            }
        }
    }
}
