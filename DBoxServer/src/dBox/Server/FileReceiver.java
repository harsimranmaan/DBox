/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Server;

import dBox.ClientAction;
import dBox.FilePacket;
import dBox.IFileServer;
import dBox.utils.ConfigManager;
import dBox.utils.CustomLogger;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author harsimran.maan
 */
public class FileReceiver extends UnicastRemoteObject implements IFileServer, Serializable
{

    private static final long serialVersionUID = 22122L;
    // actual place to receive files to
    private String directory;

    public FileReceiver() throws RemoteException
    {
        super();
    }

    @Override
    public void setDirectory(String directory) throws RemoteException
    {
        this.directory = System.getProperty("user.home") + File.separator + ConfigManager.getInstance().getPropertyValue("rootPath") + File.separator + directory;
        File theFile = new File(directory);
        theFile.mkdirs();
        CustomLogger.log("Directory path " + directory);
    }

    @Override
    public void receiveFile(String path, FilePacket packet) throws RemoteException
    {
        try
        {
            String pathOnServer = directory + File.separator + path;;
            CustomLogger.log("Path on server " + pathOnServer);
            File theFile = new File(pathOnServer);
            theFile.mkdirs();
            pathOnServer += packet.getName();
            OutputStream out = new FileOutputStream(pathOnServer);
            packet.copy(out);
            out.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public String pathSeperator() throws RemoteException
    {
        return File.separator;
    }

    @Override
    public ClientAction hasChanged(String path, HashMap<String, String> fileHashes) throws RemoteException
    {
        File theFile = new File(path);
        if (!theFile.exists())
        {
            return ClientAction.DELETE;
        }
//        else
//        {
//        if(theFile.isDirectory()){
//
//        }
//        }
        return ClientAction.NOTHING;
    }
}
