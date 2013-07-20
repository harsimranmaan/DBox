/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Server;

import dBox.FilePacket;
import dBox.ServerUtils.IServersync;
import dBox.utils.Hashing;
import java.io.File;
import java.nio.file.Files;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.HashMap;

/**
 *
 * @author harsimran.maan
 */
public class ServerSyncProvider implements IServersync
{

    private final String rootPath;

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
    public FilePacket getFile(String path) throws RemoteException
    {

        Path get = Paths.get(path);
        FilePacket packet = new FilePacket(get);
        return packet;
    }
}
