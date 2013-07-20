/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox;

import com.healthmarketscience.rmiio.RemoteInputStreamServer;
import com.healthmarketscience.rmiio.SimpleRemoteInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    public FileSender(IFileServer receiver, String serverPath, Path clientPath)
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
            FileInputStream stream = new FileInputStream(clientFile.toFile());
            RemoteInputStreamServer remoteFileData = new SimpleRemoteInputStream(stream);
            FilePacket packet = new FilePacket(clientFile.getFileName().toString(), remoteFileData.export());
            receiver.receiveFile(serverPath, packet);
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(FileSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(FileSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
