/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Server;

import com.healthmarketscience.rmiio.RemoteInputStreamServer;
import com.healthmarketscience.rmiio.SimpleRemoteInputStream;
import dBox.FilePacket;
import dBox.IFileServer;
import dBox.ServerUtils.IServersync;
import dBox.utils.Hashing;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.Receiver;

/**
 *
 * @author harsimran.maan
 */
public class ServerSyncProvider implements IServersync
{

    private final String rootPath;
    private IFileServer fileServer;

    public ServerSyncProvider(String rootPath)
    {
        this.rootPath = rootPath;
    }

    private void touchAllFiles(Path path, HashMap<String, String> list)
    {

        File folder = new File(path.toString());
        File[] filelist = folder.listFiles();
        Path filepath;
        for (int i = 0; i < filelist.length; i++)
        {
            filepath = filelist[i].toPath();
            if (Files.isDirectory(filepath, NOFOLLOW_LINKS))
            {
                touchAllFiles(filepath, list);
            }
            else
            {
                list.put(filepath.toString(), Hashing.getSHAChecksum(filepath.toString()));
            }
        }
    }

    @Override
    public HashMap<String, String> getAllFileNames() throws RemoteException
    {
        HashMap<String, String> list = new HashMap<String, String>();
        Path rootPath = Paths.get(this.rootPath);
        touchAllFiles(rootPath, list);
        return list;
    }

    @Override
    public String getHash(String path) throws RemoteException
    {
        return Hashing.getSHAChecksum(path.toString());
    }

    @Override
    public void getFile(String path, String serverName, int port) throws RemoteException
    {
        setFileServer(serverName, port);
        if (fileServer != null)
        {
            try
            {
                Path get = Paths.get(path);
                FileInputStream stream = new FileInputStream(get.toFile());
                RemoteInputStreamServer remoteFileData = new SimpleRemoteInputStream(stream);
                FilePacket packet = new FilePacket(get.getFileName().toString(), remoteFileData.export());
                fileServer.receiveFile(path, packet);
                //stream.close();
            }
            catch (IOException ex)
            {
                Logger.getLogger(ServerSyncProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void setFileServer(String serverName, int port)
    {

        try
        {
            Registry registry = LocateRegistry.getRegistry(serverName, port);
            fileServer = (IFileServer) registry.lookup(IFileServer.class.getSimpleName());
        }
        catch (RemoteException ex)
        {
            fileServer = null;
            Logger.getLogger(ServerSyncProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (NotBoundException ex)
        {
            fileServer = null;
            Logger.getLogger(ServerSyncProvider.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
