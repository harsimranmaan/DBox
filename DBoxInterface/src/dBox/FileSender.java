/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox;

import dBox.utils.CustomLogger;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class represents the file object to send a file
 * <p/>
 * @author harsimran.maan
 */
public class FileSender extends Thread
{

    private String serverPath;
    private Path clientFile;
    private IFileServer receiver;

    /**
     * Initiates the FileSender properties
     * <p/>
     * @param receiver
     * @param serverPath
     * @param clientPath
     */
    public FileSender(IFileServer receiver, String serverPath, Path clientPath)
    {
        CustomLogger.log("FileSender > FileSender : serverPath " + serverPath + " clientPath " + clientPath.toString());
        this.serverPath = serverPath;
        this.clientFile = clientPath;
        this.receiver = receiver;
    }

    /**
     * Thread starts
     */
    public void run()
    {
        this.sendFile();
    }

    /**
     * The function to send a file
     */
    public void sendFile()
    {
        try
        {
            FilePacket packet = new FilePacket(clientFile);
            receiver.receiveFile(serverPath, packet);
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(FileSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
