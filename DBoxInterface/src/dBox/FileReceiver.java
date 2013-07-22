/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox;

import dBox.utils.CustomLogger;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to represent the receiving object
 * <p/>
 * @author harsimran.maan
 */
public class FileReceiver
{
    // actual place to receive files to

    private String directory;

    /**
     * Initiates directory path and create the directory
     * <p/>
     * @param directory in which the packet is to be received
     */
    public FileReceiver(String directory)
    {
        CustomLogger.log("FileReceiver > FileReceiver : directory " + directory);
        this.directory = directory;
        File theFile = new File(directory);
        theFile.mkdirs();
    }

    /**
     * The function to receive the file packet to the given file path
     * <p/>
     * @param path
     * @param packet
     * <p/>
     * @throws RemoteException
     */
    public void receiveFile(String path, FilePacket packet) throws RemoteException
    {
        try
        {
            CustomLogger.log("FileReceiver > receiveFile : path " + path);
            String pathOnServer = directory + File.separator + path;
            //  CustomLogger.log("Path on server " + pathOnServer);
            File theFile = new File(pathOnServer);
            theFile.mkdirs();
            pathOnServer += packet.getName();
            OutputStream out = new FileOutputStream(pathOnServer);
            packet.copy(out);
        }
        catch (IOException ex)
        {
            Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
