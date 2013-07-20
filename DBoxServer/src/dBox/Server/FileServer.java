/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Server;

import dBox.ClientAction;
import dBox.FileDeleter;
import dBox.FilePacket;
import dBox.FileReceiver;
import dBox.IFileServer;
import dBox.utils.ConfigManager;
import dBox.utils.CustomLogger;
import dBox.utils.Hashing;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
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

    public FileServer() throws RemoteException
    {
        super();
    }

    @Override
    public void setDirectory(String directory) throws RemoteException
    {
        this.directory = System.getProperty("user.home") + File.separator + ConfigManager.getInstance().getPropertyValue("rootPath") + File.separator + directory;
        this.receiver = new FileReceiver(this.directory);
    }

    @Override
    public void receiveFile(String path, FilePacket packet) throws RemoteException
    {
        receiver.receiveFile(path, packet);
    }

    @Override
    public String pathSeperator() throws RemoteException
    {
        return File.separator;
    }

    @Override
    public ClientAction actionOnModify(String path, String fileHash, String oldHash) throws RemoteException
    {
        if (path.equals("a.txt"))
        {
            return ClientAction.DOWNLOAD;
        }
        File theFile = new File(path);
        if (!theFile.exists())
        {
            if (fileHash.equals(oldHash))
            {
                return ClientAction.DELETE;
            }
            else
            {
                return ClientAction.UPLOAD;
            }
        }
        else
        {
            String serverHash = Hashing.getSHAChecksum(this.directory + File.separator + path);
            CustomLogger.log("On Server hash " + serverHash);
            if (serverHash.equals(fileHash))
            {
                return ClientAction.NOTHING;
            }
            else
            {
                if (serverHash.equals(oldHash))
                {
                    return ClientAction.UPLOAD;
                }
                else
                {
                    return ClientAction.CONFLICT;
                }
            }
        }
    }

    @Override
    public ClientAction actionOnDelete(String path, String oldHash) throws RemoteException
    {
        String filePath = this.directory + File.separator + path;
        File theFile = new File(filePath);
        if (!theFile.exists())
        {
            return ClientAction.NOTHING;
        }
        else
        {


            String serverHash = Hashing.getSHAChecksum(filePath);
            System.out.println("s" + serverHash + " o " + oldHash);
            if (serverHash.equals(oldHash))
            {
                try
                {
                    //remove file
                    CustomLogger.log("Deleting " + filePath);
                    FileDeleter.delete(Paths.get(filePath), Paths.get(this.directory));
                }
                catch (IOException ex)
                {
                    Logger.getLogger(FileServer.class.getName()).log(Level.SEVERE, null, ex);
                }
                return ClientAction.NOTHING;
            }
            else
            {
                return ClientAction.DOWNLOAD;
            }
        }
    }

    @Override
    public FilePacket download(String path) throws RemoteException
    {
        Path myPath = Paths.get(this.directory + File.separator + path);
        System.out.println(myPath.toString());
        return new FilePacket(myPath);
    }
}
