/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox;

import java.nio.file.Path;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author harsimran.maan
 */
public class FileSender extends Thread
{

    private String serverPath;
    private Path clientFile;
    private IFileReceiver receiver;

    public FileSender(IFileReceiver receiver, String serverPath, Path clientPath)
    {
        this.serverPath = serverPath;
        this.clientFile = clientPath;
        this.receiver = receiver;
    }

    public void run()
    {
        this.sendFile();
    }

    public void sendFile()
    {
        try
        {
            FilePacket packet = new FilePacket(clientFile.toString());
            // packet.readIn();
            receiver.receiveFile(serverPath, packet);
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(FileSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
