/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Server;

import dBox.FilePacket;
import dBox.IFileReceiver;
import dBox.utils.ConfigManager;
import dBox.utils.CustomLogger;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author harsimran.maan
 */
public class FileReceiver extends UnicastRemoteObject implements IFileReceiver, Serializable
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
        this.directory = ConfigManager.getInstance().getPropertyValue("rootPath") + File.separator + directory;
        File theFile = new File(directory);
//        if (!theFile.isDirectory())
//        {
        theFile.mkdirs();
        //  }
        CustomLogger.log("Directory path " + directory);
    }

    @Override
    public void receiveFile(String path, FilePacket packet) throws RemoteException
    {
        try
        {
            String pathOnServer = directory;
            CustomLogger.log("Path on server " + pathOnServer);
            File theFile = new File(pathOnServer);
            theFile.mkdirs();
            pathOnServer += File.separator + path;
            OutputStream out = new FileOutputStream(pathOnServer);
            packet.writeTo(out);
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String pathSeperator() throws RemoteException
    {
        return File.separator;
    }
}
